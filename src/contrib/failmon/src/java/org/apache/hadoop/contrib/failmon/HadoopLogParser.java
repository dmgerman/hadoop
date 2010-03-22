begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
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
name|Calendar
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

begin_comment
comment|/**********************************************************  * An object of this class parses a Hadoop log file to create  * appropriate EventRecords. The log file can either be the log   * of a NameNode or JobTracker or DataNode or TaskTracker.  *   **********************************************************/
end_comment

begin_class
DECL|class|HadoopLogParser
specifier|public
class|class
name|HadoopLogParser
extends|extends
name|LogParser
block|{
comment|/**    * Create a new parser object and try to find the hostname    * of the node that generated the log    */
DECL|method|HadoopLogParser (String fname)
specifier|public
name|HadoopLogParser
parameter_list|(
name|String
name|fname
parameter_list|)
block|{
name|super
argument_list|(
name|fname
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|dateformat
operator|=
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"log.hadoop.dateformat"
argument_list|)
operator|)
operator|==
literal|null
condition|)
name|dateformat
operator|=
literal|"\\d{4}-\\d{2}-\\d{2}"
expr_stmt|;
if|if
condition|(
operator|(
name|timeformat
operator|=
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"log.hadoop.timeformat"
argument_list|)
operator|)
operator|==
literal|null
condition|)
name|timeformat
operator|=
literal|"\\d{2}:\\d{2}:\\d{2}"
expr_stmt|;
name|findHostname
argument_list|()
expr_stmt|;
block|}
comment|/**    * Parses one line of the log. If the line contains a valid     * log entry, then an appropriate EventRecord is returned, after all    * relevant fields have been parsed.    *    *  @param line the log line to be parsed    *    *  @return the EventRecord representing the log entry of the line. If     *  the line does not contain a valid log entry, then the EventRecord     *  returned has isValid() = false. When the end-of-file has been reached,    *  null is returned to the caller.    */
DECL|method|parseLine (String line)
specifier|public
name|EventRecord
name|parseLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
block|{
name|EventRecord
name|retval
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
comment|// process line
name|String
name|patternStr
init|=
literal|"("
operator|+
name|dateformat
operator|+
literal|")"
decl_stmt|;
name|patternStr
operator|+=
literal|"\\s+"
expr_stmt|;
name|patternStr
operator|+=
literal|"("
operator|+
name|timeformat
operator|+
literal|")"
expr_stmt|;
name|patternStr
operator|+=
literal|".{4}\\s(\\w*)\\s"
expr_stmt|;
comment|// for logLevel
name|patternStr
operator|+=
literal|"\\s*([\\w+\\.?]+)"
expr_stmt|;
comment|// for source
name|patternStr
operator|+=
literal|":\\s+(.+)"
expr_stmt|;
comment|// for the message
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|patternStr
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|(
literal|0
argument_list|)
operator|&&
name|matcher
operator|.
name|groupCount
argument_list|()
operator|>=
literal|5
condition|)
block|{
name|retval
operator|=
operator|new
name|EventRecord
argument_list|(
name|hostname
argument_list|,
name|ips
argument_list|,
name|parseDate
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
literal|"HadoopLog"
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|,
comment|// loglevel
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|,
comment|// source
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// message
block|}
else|else
block|{
name|retval
operator|=
operator|new
name|EventRecord
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|retval
return|;
block|}
comment|/**    * Parse a date found in the Hadoop log.    *     * @return a Calendar representing the date    */
DECL|method|parseDate (String strDate, String strTime)
specifier|protected
name|Calendar
name|parseDate
parameter_list|(
name|String
name|strDate
parameter_list|,
name|String
name|strTime
parameter_list|)
block|{
name|Calendar
name|retval
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|// set date
name|String
index|[]
name|fields
init|=
name|strDate
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MONTH
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DATE
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// set time
name|fields
operator|=
name|strTime
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|retval
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|retval
return|;
block|}
comment|/**    * Attempt to determine the hostname of the node that created the    * log file. This information can be found in the STARTUP_MSG lines     * of the Hadoop log, which are emitted when the node starts.    *     */
DECL|method|findHostname ()
specifier|private
name|void
name|findHostname
parameter_list|()
block|{
name|String
name|startupInfo
init|=
name|Environment
operator|.
name|runCommandGeneric
argument_list|(
literal|"grep --max-count=1 STARTUP_MSG:\\s*host "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s+(\\w+/.+)\\s+"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|startupInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|(
literal|0
argument_list|)
condition|)
block|{
name|hostname
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
name|ips
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
name|ips
index|[
literal|0
index|]
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
comment|/**    * Return a String with information about this class    *     * @return A String describing this class    */
DECL|method|getInfo ()
specifier|public
name|String
name|getInfo
parameter_list|()
block|{
return|return
operator|(
literal|"Hadoop Log Parser for file: "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|)
return|;
block|}
block|}
end_class

end_unit

