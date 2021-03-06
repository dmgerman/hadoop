begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
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
name|Splitter
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
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|io
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * This {@link AuditCommandParser} is used to read commands from an audit log in  * the original format audit logs are produced in with a standard configuration.  * It requires setting the {@value AUDIT_START_TIMESTAMP_KEY} configuration to  * specify what the start time of the audit log was to determine when events  * occurred relative to this start time.  *<p>  * By default, this assumes that the audit log is in the default log format  * set up by Hadoop, like:  *<pre>{@code  *   1970-01-01 00:00:00,000 INFO FSNamesystem.audit: allowed=true ...  * }</pre>  * You can adjust this parsing behavior using the various configurations  * available.  */
end_comment

begin_class
DECL|class|AuditLogDirectParser
specifier|public
class|class
name|AuditLogDirectParser
implements|implements
name|AuditCommandParser
block|{
comment|/** See class Javadoc for more detail. */
DECL|field|AUDIT_START_TIMESTAMP_KEY
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_START_TIMESTAMP_KEY
init|=
literal|"auditreplay.log-start-time.ms"
decl_stmt|;
comment|/**    * The format string used to parse the date which is present in the audit    * log. This must be a format understood by {@link SimpleDateFormat}.    */
DECL|field|AUDIT_LOG_DATE_FORMAT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_DATE_FORMAT_KEY
init|=
literal|"auditreplay.log-date.format"
decl_stmt|;
DECL|field|AUDIT_LOG_DATE_FORMAT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_DATE_FORMAT_DEFAULT
init|=
literal|"yyyy-MM-dd HH:mm:ss,SSS"
decl_stmt|;
comment|/**    * The time zone to use when parsing the audit log timestamp. This must    * be a format recognized by {@link TimeZone#getTimeZone(String)}.    */
DECL|field|AUDIT_LOG_DATE_TIME_ZONE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_DATE_TIME_ZONE_KEY
init|=
literal|"auditreplay.log-date.time-zone"
decl_stmt|;
DECL|field|AUDIT_LOG_DATE_TIME_ZONE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_DATE_TIME_ZONE_DEFAULT
init|=
literal|"UTC"
decl_stmt|;
comment|/**    * The regex to use when parsing the audit log lines. This should match    * against a single log line, and create two named capture groups. One    * must be titled "timestamp" and return a timestamp which can be parsed    * by a {@link DateFormat date formatter}. The other must be titled "message"    * and return the audit content, such as "allowed=true ugi=user ...". See    * {@link #AUDIT_LOG_PARSE_REGEX_DEFAULT} for an example.    */
DECL|field|AUDIT_LOG_PARSE_REGEX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_PARSE_REGEX_KEY
init|=
literal|"auditreplay.log-parse-regex"
decl_stmt|;
DECL|field|AUDIT_LOG_PARSE_REGEX_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|AUDIT_LOG_PARSE_REGEX_DEFAULT
init|=
literal|"^(?<timestamp>.+?) INFO [^:]+: (?<message>.+)$"
decl_stmt|;
DECL|field|SPACE_SPLITTER
specifier|private
specifier|static
specifier|final
name|Splitter
name|SPACE_SPLITTER
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
decl_stmt|;
DECL|field|startTimestamp
specifier|private
name|long
name|startTimestamp
decl_stmt|;
DECL|field|dateFormat
specifier|private
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|field|logLineParseRegex
specifier|private
name|Pattern
name|logLineParseRegex
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|startTimestamp
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|startTimestamp
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid or missing audit start timestamp: "
operator|+
name|startTimestamp
argument_list|)
throw|;
block|}
name|dateFormat
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|AUDIT_LOG_DATE_FORMAT_KEY
argument_list|,
name|AUDIT_LOG_DATE_FORMAT_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|timeZoneString
init|=
name|conf
operator|.
name|get
argument_list|(
name|AUDIT_LOG_DATE_TIME_ZONE_KEY
argument_list|,
name|AUDIT_LOG_DATE_TIME_ZONE_DEFAULT
argument_list|)
decl_stmt|;
name|dateFormat
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|timeZoneString
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|logLineParseRegexString
init|=
name|conf
operator|.
name|get
argument_list|(
name|AUDIT_LOG_PARSE_REGEX_KEY
argument_list|,
name|AUDIT_LOG_PARSE_REGEX_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|logLineParseRegexString
operator|.
name|contains
argument_list|(
literal|"(?<timestamp>"
argument_list|)
operator|&&
name|logLineParseRegexString
operator|.
name|contains
argument_list|(
literal|"(?<message>"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must configure regex with named "
operator|+
literal|"capture groups 'timestamp' and 'message'"
argument_list|)
throw|;
block|}
name|logLineParseRegex
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|logLineParseRegexString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse (Text inputLine, Function<Long, Long> relativeToAbsolute)
specifier|public
name|AuditReplayCommand
name|parse
parameter_list|(
name|Text
name|inputLine
parameter_list|,
name|Function
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|relativeToAbsolute
parameter_list|)
throws|throws
name|IOException
block|{
name|Matcher
name|m
init|=
name|logLineParseRegex
operator|.
name|matcher
argument_list|(
name|inputLine
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to find valid message pattern from audit log line: `"
operator|+
name|inputLine
operator|+
literal|"` using regex `"
operator|+
name|logLineParseRegex
operator|+
literal|"`"
argument_list|)
throw|;
block|}
name|long
name|relativeTimestamp
decl_stmt|;
try|try
block|{
name|relativeTimestamp
operator|=
name|dateFormat
operator|.
name|parse
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|"timestamp"
argument_list|)
argument_list|)
operator|.
name|getTime
argument_list|()
operator|-
name|startTimestamp
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|p
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception while parsing timestamp from audit log line: `"
operator|+
name|inputLine
operator|+
literal|"`"
argument_list|,
name|p
argument_list|)
throw|;
block|}
comment|// Sanitize the = in the rename options field into a : so we can split on =
name|String
name|auditMessageSanitized
init|=
name|m
operator|.
name|group
argument_list|(
literal|"message"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"(options="
argument_list|,
literal|"(options:"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameterMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|auditMessageSanitizedList
init|=
name|auditMessageSanitized
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|auditMessage
range|:
name|auditMessageSanitizedList
control|)
block|{
name|String
index|[]
name|splitMessage
init|=
name|auditMessage
operator|.
name|split
argument_list|(
literal|"="
argument_list|,
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|parameterMap
operator|.
name|put
argument_list|(
name|splitMessage
index|[
literal|0
index|]
argument_list|,
name|splitMessage
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception while parsing a message from audit log line: `"
operator|+
name|inputLine
operator|+
literal|"`"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|AuditReplayCommand
argument_list|(
name|relativeToAbsolute
operator|.
name|apply
argument_list|(
name|relativeTimestamp
argument_list|)
argument_list|,
comment|// Split the UGI on space to remove the auth and proxy portions of it
name|SPACE_SPLITTER
operator|.
name|split
argument_list|(
name|parameterMap
operator|.
name|get
argument_list|(
literal|"ugi"
argument_list|)
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|parameterMap
operator|.
name|get
argument_list|(
literal|"cmd"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"(options:"
argument_list|,
literal|"(options="
argument_list|)
argument_list|,
name|parameterMap
operator|.
name|get
argument_list|(
literal|"src"
argument_list|)
argument_list|,
name|parameterMap
operator|.
name|get
argument_list|(
literal|"dst"
argument_list|)
argument_list|,
name|parameterMap
operator|.
name|get
argument_list|(
literal|"ip"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

