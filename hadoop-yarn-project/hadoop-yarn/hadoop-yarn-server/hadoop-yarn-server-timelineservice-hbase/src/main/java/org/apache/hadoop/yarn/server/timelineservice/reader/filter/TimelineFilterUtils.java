begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader.filter
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
name|reader
operator|.
name|filter
package|;
end_package

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
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|filter
operator|.
name|BinaryComparator
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
name|filter
operator|.
name|BinaryPrefixComparator
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
name|filter
operator|.
name|FamilyFilter
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
name|filter
operator|.
name|CompareFilter
operator|.
name|CompareOp
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
name|filter
operator|.
name|Filter
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
name|filter
operator|.
name|FilterList
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
name|filter
operator|.
name|FilterList
operator|.
name|Operator
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
name|common
operator|.
name|Column
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
name|common
operator|.
name|ColumnFamily
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
name|common
operator|.
name|ColumnPrefix
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
name|filter
operator|.
name|QualifierFilter
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
name|filter
operator|.
name|SingleColumnValueFilter
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

begin_comment
comment|/**  * Set of utility methods used by timeline filter classes.  */
end_comment

begin_class
DECL|class|TimelineFilterUtils
specifier|public
specifier|final
class|class
name|TimelineFilterUtils
block|{
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
name|TimelineFilterUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TimelineFilterUtils ()
specifier|private
name|TimelineFilterUtils
parameter_list|()
block|{   }
comment|/**    * Returns the equivalent HBase filter list's {@link Operator}.    *    * @param op timeline filter list operator.    * @return HBase filter list's Operator.    */
DECL|method|getHBaseOperator (TimelineFilterList.Operator op)
specifier|private
specifier|static
name|Operator
name|getHBaseOperator
parameter_list|(
name|TimelineFilterList
operator|.
name|Operator
name|op
parameter_list|)
block|{
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|AND
case|:
return|return
name|Operator
operator|.
name|MUST_PASS_ALL
return|;
case|case
name|OR
case|:
return|return
name|Operator
operator|.
name|MUST_PASS_ONE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid operator"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the equivalent HBase compare filter's {@link CompareOp}.    *    * @param op timeline compare op.    * @return HBase compare filter's CompareOp.    */
DECL|method|getHBaseCompareOp ( TimelineCompareOp op)
specifier|private
specifier|static
name|CompareOp
name|getHBaseCompareOp
parameter_list|(
name|TimelineCompareOp
name|op
parameter_list|)
block|{
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|LESS_THAN
case|:
return|return
name|CompareOp
operator|.
name|LESS
return|;
case|case
name|LESS_OR_EQUAL
case|:
return|return
name|CompareOp
operator|.
name|LESS_OR_EQUAL
return|;
case|case
name|EQUAL
case|:
return|return
name|CompareOp
operator|.
name|EQUAL
return|;
case|case
name|NOT_EQUAL
case|:
return|return
name|CompareOp
operator|.
name|NOT_EQUAL
return|;
case|case
name|GREATER_OR_EQUAL
case|:
return|return
name|CompareOp
operator|.
name|GREATER_OR_EQUAL
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|CompareOp
operator|.
name|GREATER
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid compare operator"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Converts a {@link TimelinePrefixFilter} to an equivalent HBase    * {@link QualifierFilter}.    * @param colPrefix    * @param filter    * @return a {@link QualifierFilter} object    */
DECL|method|createHBaseColQualPrefixFilter ( ColumnPrefix<T> colPrefix, TimelinePrefixFilter filter)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Filter
name|createHBaseColQualPrefixFilter
parameter_list|(
name|ColumnPrefix
argument_list|<
name|T
argument_list|>
name|colPrefix
parameter_list|,
name|TimelinePrefixFilter
name|filter
parameter_list|)
block|{
return|return
operator|new
name|QualifierFilter
argument_list|(
name|getHBaseCompareOp
argument_list|(
name|filter
operator|.
name|getCompareOp
argument_list|()
argument_list|)
argument_list|,
operator|new
name|BinaryPrefixComparator
argument_list|(
name|colPrefix
operator|.
name|getColumnPrefixBytes
argument_list|(
name|filter
operator|.
name|getPrefix
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create a HBase {@link QualifierFilter} for the passed column prefix and    * compare op.    *    * @param<T> Describes the type of column prefix.    * @param compareOp compare op.    * @param columnPrefix column prefix.    * @return a column qualifier filter.    */
DECL|method|createHBaseQualifierFilter (CompareOp compareOp, ColumnPrefix<T> columnPrefix)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Filter
name|createHBaseQualifierFilter
parameter_list|(
name|CompareOp
name|compareOp
parameter_list|,
name|ColumnPrefix
argument_list|<
name|T
argument_list|>
name|columnPrefix
parameter_list|)
block|{
return|return
operator|new
name|QualifierFilter
argument_list|(
name|compareOp
argument_list|,
operator|new
name|BinaryPrefixComparator
argument_list|(
name|columnPrefix
operator|.
name|getColumnPrefixBytes
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Create filters for confs or metrics to retrieve. This list includes a    * configs/metrics family filter and relevant filters for confs/metrics to    * retrieve, if present.    *    * @param<T> Describes the type of column prefix.    * @param confsOrMetricToRetrieve configs/metrics to retrieve.    * @param columnFamily config or metric column family.    * @param columnPrefix config or metric column prefix.    * @return a filter list.    * @throws IOException if any problem occurs while creating the filters.    */
DECL|method|createFilterForConfsOrMetricsToRetrieve ( TimelineFilterList confsOrMetricToRetrieve, ColumnFamily<T> columnFamily, ColumnPrefix<T> columnPrefix)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Filter
name|createFilterForConfsOrMetricsToRetrieve
parameter_list|(
name|TimelineFilterList
name|confsOrMetricToRetrieve
parameter_list|,
name|ColumnFamily
argument_list|<
name|T
argument_list|>
name|columnFamily
parameter_list|,
name|ColumnPrefix
argument_list|<
name|T
argument_list|>
name|columnPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|Filter
name|familyFilter
init|=
operator|new
name|FamilyFilter
argument_list|(
name|CompareOp
operator|.
name|EQUAL
argument_list|,
operator|new
name|BinaryComparator
argument_list|(
name|columnFamily
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|confsOrMetricToRetrieve
operator|!=
literal|null
operator|&&
operator|!
name|confsOrMetricToRetrieve
operator|.
name|getFilterList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If confsOrMetricsToRetrive are specified, create a filter list based
comment|// on it and family filter.
name|FilterList
name|filter
init|=
operator|new
name|FilterList
argument_list|(
name|familyFilter
argument_list|)
decl_stmt|;
name|filter
operator|.
name|addFilter
argument_list|(
name|createHBaseFilterList
argument_list|(
name|columnPrefix
argument_list|,
name|confsOrMetricToRetrieve
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
else|else
block|{
comment|// Only the family filter needs to be added.
return|return
name|familyFilter
return|;
block|}
block|}
comment|/**    * Create 2 HBase {@link SingleColumnValueFilter} filters for the specified    * value range represented by start and end value and wraps them inside a    * filter list. Start and end value should not be null.    *    * @param<T> Describes the type of column prefix.    * @param column Column for which single column value filter is to be created.    * @param startValue Start value.    * @param endValue End value.    * @return 2 single column value filters wrapped in a filter list.    * @throws IOException if any problem is encountered while encoding value.    */
DECL|method|createSingleColValueFiltersByRange ( Column<T> column, Object startValue, Object endValue)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|FilterList
name|createSingleColValueFiltersByRange
parameter_list|(
name|Column
argument_list|<
name|T
argument_list|>
name|column
parameter_list|,
name|Object
name|startValue
parameter_list|,
name|Object
name|endValue
parameter_list|)
throws|throws
name|IOException
block|{
name|FilterList
name|list
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
name|Filter
name|singleColValFilterStart
init|=
name|createHBaseSingleColValueFilter
argument_list|(
name|column
operator|.
name|getColumnFamilyBytes
argument_list|()
argument_list|,
name|column
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|,
name|column
operator|.
name|getValueConverter
argument_list|()
operator|.
name|encodeValue
argument_list|(
name|startValue
argument_list|)
argument_list|,
name|CompareOp
operator|.
name|GREATER_OR_EQUAL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|singleColValFilterStart
argument_list|)
expr_stmt|;
name|Filter
name|singleColValFilterEnd
init|=
name|createHBaseSingleColValueFilter
argument_list|(
name|column
operator|.
name|getColumnFamilyBytes
argument_list|()
argument_list|,
name|column
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|,
name|column
operator|.
name|getValueConverter
argument_list|()
operator|.
name|encodeValue
argument_list|(
name|endValue
argument_list|)
argument_list|,
name|CompareOp
operator|.
name|LESS_OR_EQUAL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|singleColValFilterEnd
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**    * Creates a HBase {@link SingleColumnValueFilter}.    *    * @param columnFamily Column Family represented as bytes.    * @param columnQualifier Column Qualifier represented as bytes.    * @param value Value.    * @param compareOp Compare operator.    * @param filterIfMissing This flag decides if we should filter the row if the    *     specified column is missing. This is based on the filter's keyMustExist    *     field.    * @return a {@link SingleColumnValueFilter} object    * @throws IOException    */
DECL|method|createHBaseSingleColValueFilter ( byte[] columnFamily, byte[] columnQualifier, byte[] value, CompareOp compareOp, boolean filterIfMissing)
specifier|private
specifier|static
name|SingleColumnValueFilter
name|createHBaseSingleColValueFilter
parameter_list|(
name|byte
index|[]
name|columnFamily
parameter_list|,
name|byte
index|[]
name|columnQualifier
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|CompareOp
name|compareOp
parameter_list|,
name|boolean
name|filterIfMissing
parameter_list|)
throws|throws
name|IOException
block|{
name|SingleColumnValueFilter
name|singleColValFilter
init|=
operator|new
name|SingleColumnValueFilter
argument_list|(
name|columnFamily
argument_list|,
name|columnQualifier
argument_list|,
name|compareOp
argument_list|,
operator|new
name|BinaryComparator
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|singleColValFilter
operator|.
name|setLatestVersionOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|singleColValFilter
operator|.
name|setFilterIfMissing
argument_list|(
name|filterIfMissing
argument_list|)
expr_stmt|;
return|return
name|singleColValFilter
return|;
block|}
comment|/**    * Fetch columns from filter list containing exists and multivalue equality    * filters. This is done to fetch only required columns from back-end and    * then match event filters or relationships in reader.    *    * @param filterList filter list.    * @return set of columns.    */
DECL|method|fetchColumnsFromFilterList ( TimelineFilterList filterList)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|fetchColumnsFromFilterList
parameter_list|(
name|TimelineFilterList
name|filterList
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|strSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineFilter
name|filter
range|:
name|filterList
operator|.
name|getFilterList
argument_list|()
control|)
block|{
switch|switch
condition|(
name|filter
operator|.
name|getFilterType
argument_list|()
condition|)
block|{
case|case
name|LIST
case|:
name|strSet
operator|.
name|addAll
argument_list|(
name|fetchColumnsFromFilterList
argument_list|(
operator|(
name|TimelineFilterList
operator|)
name|filter
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KEY_VALUES
case|:
name|strSet
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TimelineKeyValuesFilter
operator|)
name|filter
operator|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXISTS
case|:
name|strSet
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TimelineExistsFilter
operator|)
name|filter
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|info
argument_list|(
literal|"Unexpected filter type "
operator|+
name|filter
operator|.
name|getFilterType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|strSet
return|;
block|}
comment|/**    * Creates equivalent HBase {@link FilterList} from {@link TimelineFilterList}    * while converting different timeline filters(of type {@link TimelineFilter})    * into their equivalent HBase filters.    *    * @param<T> Describes the type of column prefix.    * @param colPrefix column prefix which will be used for conversion.    * @param filterList timeline filter list which has to be converted.    * @return A {@link FilterList} object.    * @throws IOException if any problem occurs while creating the filter list.    */
DECL|method|createHBaseFilterList (ColumnPrefix<T> colPrefix, TimelineFilterList filterList)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|FilterList
name|createHBaseFilterList
parameter_list|(
name|ColumnPrefix
argument_list|<
name|T
argument_list|>
name|colPrefix
parameter_list|,
name|TimelineFilterList
name|filterList
parameter_list|)
throws|throws
name|IOException
block|{
name|FilterList
name|list
init|=
operator|new
name|FilterList
argument_list|(
name|getHBaseOperator
argument_list|(
name|filterList
operator|.
name|getOperator
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|TimelineFilter
name|filter
range|:
name|filterList
operator|.
name|getFilterList
argument_list|()
control|)
block|{
switch|switch
condition|(
name|filter
operator|.
name|getFilterType
argument_list|()
condition|)
block|{
case|case
name|LIST
case|:
name|list
operator|.
name|addFilter
argument_list|(
name|createHBaseFilterList
argument_list|(
name|colPrefix
argument_list|,
operator|(
name|TimelineFilterList
operator|)
name|filter
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PREFIX
case|:
name|list
operator|.
name|addFilter
argument_list|(
name|createHBaseColQualPrefixFilter
argument_list|(
name|colPrefix
argument_list|,
operator|(
name|TimelinePrefixFilter
operator|)
name|filter
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|COMPARE
case|:
name|TimelineCompareFilter
name|compareFilter
init|=
operator|(
name|TimelineCompareFilter
operator|)
name|filter
decl_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|createHBaseSingleColValueFilter
argument_list|(
name|colPrefix
operator|.
name|getColumnFamilyBytes
argument_list|()
argument_list|,
name|colPrefix
operator|.
name|getColumnPrefixBytes
argument_list|(
name|compareFilter
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|colPrefix
operator|.
name|getValueConverter
argument_list|()
operator|.
name|encodeValue
argument_list|(
name|compareFilter
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|getHBaseCompareOp
argument_list|(
name|compareFilter
operator|.
name|getCompareOp
argument_list|()
argument_list|)
argument_list|,
name|compareFilter
operator|.
name|getKeyMustExist
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KEY_VALUE
case|:
name|TimelineKeyValueFilter
name|kvFilter
init|=
operator|(
name|TimelineKeyValueFilter
operator|)
name|filter
decl_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|createHBaseSingleColValueFilter
argument_list|(
name|colPrefix
operator|.
name|getColumnFamilyBytes
argument_list|()
argument_list|,
name|colPrefix
operator|.
name|getColumnPrefixBytes
argument_list|(
name|kvFilter
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|colPrefix
operator|.
name|getValueConverter
argument_list|()
operator|.
name|encodeValue
argument_list|(
name|kvFilter
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|getHBaseCompareOp
argument_list|(
name|kvFilter
operator|.
name|getCompareOp
argument_list|()
argument_list|)
argument_list|,
name|kvFilter
operator|.
name|getKeyMustExist
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|info
argument_list|(
literal|"Unexpected filter type "
operator|+
name|filter
operator|.
name|getFilterType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

