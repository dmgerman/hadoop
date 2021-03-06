begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|io
operator|.
name|IOUtils
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
name|StringUtils
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
name|security
operator|.
name|UserGroupInformation
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
name|reader
operator|.
name|filter
operator|.
name|TimelineFilterList
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
name|TimelineReader
operator|.
name|Field
import|;
end_import

begin_comment
comment|/**  * Set of utility methods to be used by timeline reader web services.  */
end_comment

begin_class
DECL|class|TimelineReaderWebServicesUtils
specifier|public
specifier|final
class|class
name|TimelineReaderWebServicesUtils
block|{
DECL|method|TimelineReaderWebServicesUtils ()
specifier|private
name|TimelineReaderWebServicesUtils
parameter_list|()
block|{   }
comment|/**    * Parse the passed context information represented as strings and convert    * into a {@link TimelineReaderContext} object.    * @param clusterId Cluster Id.    * @param userId User Id.    * @param flowName Flow Name.    * @param flowRunId Run id for the flow.    * @param appId App Id.    * @param entityType Entity Type.    * @param entityId Entity Id.    * @return a {@link TimelineReaderContext} object.    */
DECL|method|createTimelineReaderContext (String clusterId, String userId, String flowName, String flowRunId, String appId, String entityType, String entityIdPrefix, String entityId)
specifier|static
name|TimelineReaderContext
name|createTimelineReaderContext
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|String
name|entityIdPrefix
parameter_list|,
name|String
name|entityId
parameter_list|)
block|{
return|return
operator|new
name|TimelineReaderContext
argument_list|(
name|parseStr
argument_list|(
name|clusterId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|userId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|flowName
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|flowRunId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|appId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|entityIdPrefix
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createTimelineReaderContext (String clusterId, String userId, String flowName, String flowRunId, String appId, String entityType, String entityIdPrefix, String entityId, String doAsUser)
specifier|static
name|TimelineReaderContext
name|createTimelineReaderContext
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|String
name|entityIdPrefix
parameter_list|,
name|String
name|entityId
parameter_list|,
name|String
name|doAsUser
parameter_list|)
block|{
return|return
operator|new
name|TimelineReaderContext
argument_list|(
name|parseStr
argument_list|(
name|clusterId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|userId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|flowName
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|flowRunId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|appId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|entityIdPrefix
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|doAsUser
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parse the passed filters represented as strings and convert them into a    * {@link TimelineEntityFilters} object.    * @param limit Limit to number of entities to return.    * @param createdTimeStart Created time start for the entities to return.    * @param createdTimeEnd Created time end for the entities to return.    * @param relatesTo Entities to return must match relatesTo.    * @param isRelatedTo Entities to return must match isRelatedTo.    * @param infofilters Entities to return must match these info filters.    * @param conffilters Entities to return must match these metric filters.    * @param metricfilters Entities to return must match these metric filters.    * @param eventfilters Entities to return must match these event filters.    * @return a {@link TimelineEntityFilters} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|createTimelineEntityFilters (String limit, String createdTimeStart, String createdTimeEnd, String relatesTo, String isRelatedTo, String infofilters, String conffilters, String metricfilters, String eventfilters, String fromid)
specifier|static
name|TimelineEntityFilters
name|createTimelineEntityFilters
parameter_list|(
name|String
name|limit
parameter_list|,
name|String
name|createdTimeStart
parameter_list|,
name|String
name|createdTimeEnd
parameter_list|,
name|String
name|relatesTo
parameter_list|,
name|String
name|isRelatedTo
parameter_list|,
name|String
name|infofilters
parameter_list|,
name|String
name|conffilters
parameter_list|,
name|String
name|metricfilters
parameter_list|,
name|String
name|eventfilters
parameter_list|,
name|String
name|fromid
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|createTimelineEntityFilters
argument_list|(
name|limit
argument_list|,
name|parseLongStr
argument_list|(
name|createdTimeStart
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|createdTimeEnd
argument_list|)
argument_list|,
name|relatesTo
argument_list|,
name|isRelatedTo
argument_list|,
name|infofilters
argument_list|,
name|conffilters
argument_list|,
name|metricfilters
argument_list|,
name|eventfilters
argument_list|,
name|fromid
argument_list|)
return|;
block|}
comment|/**    * Parse the passed filters represented as strings and convert them into a    * {@link TimelineEntityFilters} object.    * @param limit Limit to number of entities to return.    * @param createdTimeStart Created time start for the entities to return.    * @param createdTimeEnd Created time end for the entities to return.    * @param relatesTo Entities to return must match relatesTo.    * @param isRelatedTo Entities to return must match isRelatedTo.    * @param infofilters Entities to return must match these info filters.    * @param conffilters Entities to return must match these metric filters.    * @param metricfilters Entities to return must match these metric filters.    * @param eventfilters Entities to return must match these event filters.    * @return a {@link TimelineEntityFilters} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|createTimelineEntityFilters (String limit, Long createdTimeStart, Long createdTimeEnd, String relatesTo, String isRelatedTo, String infofilters, String conffilters, String metricfilters, String eventfilters, String fromid)
specifier|static
name|TimelineEntityFilters
name|createTimelineEntityFilters
parameter_list|(
name|String
name|limit
parameter_list|,
name|Long
name|createdTimeStart
parameter_list|,
name|Long
name|createdTimeEnd
parameter_list|,
name|String
name|relatesTo
parameter_list|,
name|String
name|isRelatedTo
parameter_list|,
name|String
name|infofilters
parameter_list|,
name|String
name|conffilters
parameter_list|,
name|String
name|metricfilters
parameter_list|,
name|String
name|eventfilters
parameter_list|,
name|String
name|fromid
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
operator|new
name|TimelineEntityFilters
operator|.
name|Builder
argument_list|()
operator|.
name|entityLimit
argument_list|(
name|parseLongStr
argument_list|(
name|limit
argument_list|)
argument_list|)
operator|.
name|createdTimeBegin
argument_list|(
name|createdTimeStart
argument_list|)
operator|.
name|createTimeEnd
argument_list|(
name|createdTimeEnd
argument_list|)
operator|.
name|relatesTo
argument_list|(
name|parseRelationFilters
argument_list|(
name|relatesTo
argument_list|)
argument_list|)
operator|.
name|isRelatedTo
argument_list|(
name|parseRelationFilters
argument_list|(
name|isRelatedTo
argument_list|)
argument_list|)
operator|.
name|infoFilters
argument_list|(
name|parseKVFilters
argument_list|(
name|infofilters
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|configFilters
argument_list|(
name|parseKVFilters
argument_list|(
name|conffilters
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|metricFilters
argument_list|(
name|parseMetricFilters
argument_list|(
name|metricfilters
argument_list|)
argument_list|)
operator|.
name|eventFilters
argument_list|(
name|parseEventFilters
argument_list|(
name|eventfilters
argument_list|)
argument_list|)
operator|.
name|fromId
argument_list|(
name|parseStr
argument_list|(
name|fromid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Parse the passed fields represented as strings and convert them into a    * {@link TimelineDataToRetrieve} object.    * @param confs confs to retrieve.    * @param metrics metrics to retrieve.    * @param fields fields to retrieve.    * @param metricsLimit upper limit on number of metrics to return.    * @return a {@link TimelineDataToRetrieve} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|createTimelineDataToRetrieve (String confs, String metrics, String fields, String metricsLimit, String metricsTimeBegin, String metricsTimeEnd)
specifier|static
name|TimelineDataToRetrieve
name|createTimelineDataToRetrieve
parameter_list|(
name|String
name|confs
parameter_list|,
name|String
name|metrics
parameter_list|,
name|String
name|fields
parameter_list|,
name|String
name|metricsLimit
parameter_list|,
name|String
name|metricsTimeBegin
parameter_list|,
name|String
name|metricsTimeEnd
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
operator|new
name|TimelineDataToRetrieve
argument_list|(
name|parseDataToRetrieve
argument_list|(
name|confs
argument_list|)
argument_list|,
name|parseDataToRetrieve
argument_list|(
name|metrics
argument_list|)
argument_list|,
name|parseFieldsStr
argument_list|(
name|fields
argument_list|,
name|TimelineParseConstants
operator|.
name|COMMA_DELIMITER
argument_list|)
argument_list|,
name|parseIntStr
argument_list|(
name|metricsLimit
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|metricsTimeBegin
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|metricsTimeEnd
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parse a delimited string and convert it into a set of strings. For    * instance, if delimiter is ",", then the string should be represented as    * "value1,value2,value3".    * @param str delimited string.    * @param delimiter string is delimited by this delimiter.    * @return set of strings.    */
DECL|method|parseEventFilters (String expr)
specifier|static
name|TimelineFilterList
name|parseEventFilters
parameter_list|(
name|String
name|expr
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|parseFilters
argument_list|(
operator|new
name|TimelineParserForExistFilters
argument_list|(
name|expr
argument_list|,
name|TimelineParseConstants
operator|.
name|COMMA_CHAR
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parse relation filters.    * @param expr Relation filter expression    * @return a {@link TimelineFilterList} object.    *    * @throws Exception if any problem occurs.    */
DECL|method|parseRelationFilters (String expr)
specifier|static
name|TimelineFilterList
name|parseRelationFilters
parameter_list|(
name|String
name|expr
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|parseFilters
argument_list|(
operator|new
name|TimelineParserForRelationFilters
argument_list|(
name|expr
argument_list|,
name|TimelineParseConstants
operator|.
name|COMMA_CHAR
argument_list|,
name|TimelineParseConstants
operator|.
name|COLON_DELIMITER
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseFilters (TimelineParser parser)
specifier|private
specifier|static
name|TimelineFilterList
name|parseFilters
parameter_list|(
name|TimelineParser
name|parser
parameter_list|)
throws|throws
name|TimelineParseException
block|{
try|try
block|{
return|return
name|parser
operator|.
name|parse
argument_list|()
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Parses config and info filters.    *    * @param expr Expression to be parsed.    * @param valueAsString true, if value has to be interpreted as string, false    *     otherwise. It is true for config filters and false for info filters.    * @return a {@link TimelineFilterList} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|parseKVFilters (String expr, boolean valueAsString)
specifier|static
name|TimelineFilterList
name|parseKVFilters
parameter_list|(
name|String
name|expr
parameter_list|,
name|boolean
name|valueAsString
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|parseFilters
argument_list|(
operator|new
name|TimelineParserForKVFilters
argument_list|(
name|expr
argument_list|,
name|valueAsString
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Interprets passed string as set of fields delimited by passed delimiter.    * For instance, if delimiter is ",", then the passed string should be    * represented as "METRICS,CONFIGS" where the delimited parts of the string    * present in {@link Field}.    * @param str passed string.    * @param delimiter string delimiter.    * @return a set of {@link Field}.    */
DECL|method|parseFieldsStr (String str, String delimiter)
specifier|static
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|parseFieldsStr
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|strs
init|=
name|str
operator|.
name|split
argument_list|(
name|delimiter
argument_list|)
decl_stmt|;
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldList
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|strs
control|)
block|{
try|try
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|s
operator|+
literal|" is not a valid field."
argument_list|)
throw|;
block|}
block|}
return|return
name|fieldList
return|;
block|}
comment|/**    * Parses metric filters.    *    * @param expr Metric filter expression to be parsed.    * @return a {@link TimelineFilterList} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|parseMetricFilters (String expr)
specifier|static
name|TimelineFilterList
name|parseMetricFilters
parameter_list|(
name|String
name|expr
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|parseFilters
argument_list|(
operator|new
name|TimelineParserForNumericFilters
argument_list|(
name|expr
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Interpret passed string as a long.    * @param str Passed string.    * @return long representation if string is not null, null otherwise.    */
DECL|method|parseLongStr (String str)
specifier|static
name|Long
name|parseLongStr
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|==
literal|null
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|str
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Interpret passed string as a integer.    * @param str Passed string.    * @return integer representation if string is not null, null otherwise.    */
DECL|method|parseIntStr (String str)
specifier|static
name|Integer
name|parseIntStr
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|==
literal|null
condition|?
literal|null
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Trims the passed string if its not null.    * @param str Passed string.    * @return trimmed string if string is not null, null otherwise.    */
DECL|method|parseStr (String str)
specifier|static
name|String
name|parseStr
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|trimToNull
argument_list|(
name|str
argument_list|)
return|;
block|}
comment|/**    * Get UGI based on the remote user in the HTTP request.    *    * @param req HTTP request.    * @return UGI.    */
DECL|method|getUser (HttpServletRequest req)
specifier|public
specifier|static
name|UserGroupInformation
name|getUser
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Principal
name|princ
init|=
name|req
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
name|String
name|remoteUser
init|=
name|princ
operator|==
literal|null
condition|?
literal|null
else|:
name|princ
operator|.
name|getName
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
return|return
name|callerUGI
return|;
block|}
comment|/**    * Get username from caller UGI.    * @param callerUGI caller UGI.    * @return username.    */
DECL|method|getUserName (UserGroupInformation callerUGI)
specifier|static
name|String
name|getUserName
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|)
block|{
return|return
operator|(
operator|(
name|callerUGI
operator|!=
literal|null
operator|)
condition|?
name|callerUGI
operator|.
name|getUserName
argument_list|()
operator|.
name|trim
argument_list|()
else|:
literal|""
operator|)
return|;
block|}
comment|/**    * Parses confstoretrieve and metricstoretrieve.    * @param str String representing confs/metrics to retrieve expression.    *    * @return a {@link TimelineFilterList} object.    * @throws TimelineParseException if any problem occurs during parsing.    */
DECL|method|parseDataToRetrieve (String expr)
specifier|static
name|TimelineFilterList
name|parseDataToRetrieve
parameter_list|(
name|String
name|expr
parameter_list|)
throws|throws
name|TimelineParseException
block|{
return|return
name|parseFilters
argument_list|(
operator|new
name|TimelineParserForDataToRetrieve
argument_list|(
name|expr
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

