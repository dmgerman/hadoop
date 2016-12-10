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
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|conf
operator|.
name|Configuration
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
name|hbase
operator|.
name|Cell
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
name|hbase
operator|.
name|CellUtil
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
name|hbase
operator|.
name|HBaseConfiguration
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
name|hbase
operator|.
name|HConstants
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
name|hbase
operator|.
name|HRegionInfo
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
name|hbase
operator|.
name|KeyValue
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
name|hbase
operator|.
name|Tag
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
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
name|conf
operator|.
name|YarnConfiguration
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
name|AggregationCompactionDimension
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
name|FlowRunTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * A bunch of utility functions used in HBase TimelineService backend.  */
end_comment

begin_class
DECL|class|HBaseTimelineStorageUtils
specifier|public
specifier|final
class|class
name|HBaseTimelineStorageUtils
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
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HBaseTimelineStorageUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|HBaseTimelineStorageUtils ()
specifier|private
name|HBaseTimelineStorageUtils
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
comment|/**    * Returns the first seen aggregation operation as seen in the list of input    * tags or null otherwise.    *    * @param tags list of HBase tags.    * @return AggregationOperation    */
DECL|method|getAggregationOperationFromTagsList ( List<Tag> tags)
specifier|public
specifier|static
name|AggregationOperation
name|getAggregationOperationFromTagsList
parameter_list|(
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
parameter_list|)
block|{
for|for
control|(
name|AggregationOperation
name|aggOp
range|:
name|AggregationOperation
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Tag
name|tag
range|:
name|tags
control|)
block|{
if|if
condition|(
name|tag
operator|.
name|getType
argument_list|()
operator|==
name|aggOp
operator|.
name|getTagType
argument_list|()
condition|)
block|{
return|return
name|aggOp
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Creates a {@link Tag} from the input attribute.    *    * @param attribute Attribute from which tag has to be fetched.    * @return a HBase Tag.    */
DECL|method|getTagFromAttribute (Map.Entry<String, byte[]> attribute)
specifier|public
specifier|static
name|Tag
name|getTagFromAttribute
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|attribute
parameter_list|)
block|{
comment|// attribute could be either an Aggregation Operation or
comment|// an Aggregation Dimension
comment|// Get the Tag type from either
name|AggregationOperation
name|aggOp
init|=
name|AggregationOperation
operator|.
name|getAggregationOperation
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggOp
operator|!=
literal|null
condition|)
block|{
name|Tag
name|t
init|=
operator|new
name|Tag
argument_list|(
name|aggOp
operator|.
name|getTagType
argument_list|()
argument_list|,
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
name|AggregationCompactionDimension
name|aggCompactDim
init|=
name|AggregationCompactionDimension
operator|.
name|getAggregationCompactionDimension
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggCompactDim
operator|!=
literal|null
condition|)
block|{
name|Tag
name|t
init|=
operator|new
name|Tag
argument_list|(
name|aggCompactDim
operator|.
name|getTagType
argument_list|()
argument_list|,
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * creates a new cell based on the input cell but with the new value.    *    * @param origCell Original cell    * @param newValue new cell value    * @return cell    * @throws IOException while creating new cell.    */
DECL|method|createNewCell (Cell origCell, byte[] newValue)
specifier|public
specifier|static
name|Cell
name|createNewCell
parameter_list|(
name|Cell
name|origCell
parameter_list|,
name|byte
index|[]
name|newValue
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CellUtil
operator|.
name|createCell
argument_list|(
name|CellUtil
operator|.
name|cloneRow
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|CellUtil
operator|.
name|cloneFamily
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|CellUtil
operator|.
name|cloneQualifier
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|origCell
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|KeyValue
operator|.
name|Type
operator|.
name|Put
operator|.
name|getCode
argument_list|()
argument_list|,
name|newValue
argument_list|)
return|;
block|}
comment|/**    * creates a cell with the given inputs.    *    * @param row row of the cell to be created    * @param family column family name of the new cell    * @param qualifier qualifier for the new cell    * @param ts timestamp of the new cell    * @param newValue value of the new cell    * @param tags tags in the new cell    * @return cell    * @throws IOException while creating the cell.    */
DECL|method|createNewCell (byte[] row, byte[] family, byte[] qualifier, long ts, byte[] newValue, byte[] tags)
specifier|public
specifier|static
name|Cell
name|createNewCell
parameter_list|(
name|byte
index|[]
name|row
parameter_list|,
name|byte
index|[]
name|family
parameter_list|,
name|byte
index|[]
name|qualifier
parameter_list|,
name|long
name|ts
parameter_list|,
name|byte
index|[]
name|newValue
parameter_list|,
name|byte
index|[]
name|tags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CellUtil
operator|.
name|createCell
argument_list|(
name|row
argument_list|,
name|family
argument_list|,
name|qualifier
argument_list|,
name|ts
argument_list|,
name|KeyValue
operator|.
name|Type
operator|.
name|Put
argument_list|,
name|newValue
argument_list|,
name|tags
argument_list|)
return|;
block|}
comment|/**    * returns app id from the list of tags.    *    * @param tags cell tags to be looked into    * @return App Id as the AggregationCompactionDimension    */
DECL|method|getAggregationCompactionDimension (List<Tag> tags)
specifier|public
specifier|static
name|String
name|getAggregationCompactionDimension
parameter_list|(
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
parameter_list|)
block|{
name|String
name|appId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Tag
name|t
range|:
name|tags
control|)
block|{
if|if
condition|(
name|AggregationCompactionDimension
operator|.
name|APPLICATION_ID
operator|.
name|getTagType
argument_list|()
operator|==
name|t
operator|.
name|getType
argument_list|()
condition|)
block|{
name|appId
operator|=
name|Bytes
operator|.
name|toString
argument_list|(
name|t
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|appId
return|;
block|}
block|}
return|return
name|appId
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
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|appId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
name|sb
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
comment|/**    * @param conf Yarn configuration. Used to see if there is an explicit config    *          pointing to the HBase config file to read. It should not be null    *          or a NullPointerException will be thrown.    * @return a configuration with the HBase configuration from the classpath,    *         optionally overwritten by the timeline service configuration URL if    *         specified.    * @throws MalformedURLException if a timeline service HBase configuration URL    *           is specified but is a malformed URL.    */
DECL|method|getTimelineServiceHBaseConf (Configuration conf)
specifier|public
specifier|static
name|Configuration
name|getTimelineServiceHBaseConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|Configuration
name|hbaseConf
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
return|return
name|HBaseConfiguration
operator|.
name|create
argument_list|()
return|;
block|}
name|String
name|timelineServiceHBaseConfFileURL
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HBASE_CONFIGURATION_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|timelineServiceHBaseConfFileURL
operator|!=
literal|null
operator|&&
name|timelineServiceHBaseConfFileURL
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// create a clone so that we don't mess with out input one
name|hbaseConf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Configuration
name|plainHBaseConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|URL
name|hbaseSiteXML
init|=
operator|new
name|URL
argument_list|(
name|timelineServiceHBaseConfFileURL
argument_list|)
decl_stmt|;
name|plainHBaseConf
operator|.
name|addResource
argument_list|(
name|hbaseSiteXML
argument_list|)
expr_stmt|;
name|HBaseConfiguration
operator|.
name|merge
argument_list|(
name|hbaseConf
argument_list|,
name|plainHBaseConf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default to what is on the classpath
name|hbaseConf
operator|=
name|HBaseConfiguration
operator|.
name|create
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|hbaseConf
return|;
block|}
comment|/**    * Given a row key prefix stored in a byte array, return a byte array for its    * immediate next row key.    *    * @param rowKeyPrefix The provided row key prefix, represented in an array.    * @return the closest next row key of the provided row key.    */
DECL|method|calculateTheClosestNextRowKeyForPrefix ( byte[] rowKeyPrefix)
specifier|public
specifier|static
name|byte
index|[]
name|calculateTheClosestNextRowKeyForPrefix
parameter_list|(
name|byte
index|[]
name|rowKeyPrefix
parameter_list|)
block|{
comment|// Essentially we are treating it like an 'unsigned very very long' and
comment|// doing +1 manually.
comment|// Search for the place where the trailing 0xFFs start
name|int
name|offset
init|=
name|rowKeyPrefix
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|rowKeyPrefix
index|[
name|offset
operator|-
literal|1
index|]
operator|!=
operator|(
name|byte
operator|)
literal|0xFF
condition|)
block|{
break|break;
block|}
name|offset
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
comment|// We got an 0xFFFF... (only FFs) stopRow value which is
comment|// the last possible prefix before the end of the table.
comment|// So set it to stop at the 'end of the table'
return|return
name|HConstants
operator|.
name|EMPTY_END_ROW
return|;
block|}
comment|// Copy the right length of the original
name|byte
index|[]
name|newStopRow
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|rowKeyPrefix
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|)
decl_stmt|;
comment|// And increment the last one
name|newStopRow
index|[
name|newStopRow
operator|.
name|length
operator|-
literal|1
index|]
operator|++
expr_stmt|;
return|return
name|newStopRow
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
block|}
end_class

end_unit

