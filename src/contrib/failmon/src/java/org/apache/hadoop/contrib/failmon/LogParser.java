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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_comment
comment|/**********************************************************  * This class represents objects that provide log parsing   * functionality. Typically, such objects read log files line  * by line and for each log entry they identify, they create a   * corresponding EventRecord. In this way, disparate log files  * can be merged using the uniform format of EventRecords and can,  * thus, be processed in a uniform way.  *   **********************************************************/
end_comment

begin_class
DECL|class|LogParser
specifier|public
specifier|abstract
class|class
name|LogParser
implements|implements
name|Monitored
block|{
DECL|field|file
name|File
name|file
decl_stmt|;
DECL|field|reader
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|hostname
name|String
name|hostname
decl_stmt|;
DECL|field|ips
name|Object
index|[]
name|ips
decl_stmt|;
DECL|field|dateformat
name|String
name|dateformat
decl_stmt|;
DECL|field|timeformat
name|String
name|timeformat
decl_stmt|;
DECL|field|firstLine
specifier|private
name|String
name|firstLine
decl_stmt|;
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
comment|/**    * Create a parser that will read from the specified log file.    *     * @param fname the filename of the log file to be read    */
DECL|method|LogParser (String fname)
specifier|public
name|LogParser
parameter_list|(
name|String
name|fname
parameter_list|)
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|fname
argument_list|)
expr_stmt|;
name|ParseState
name|ps
init|=
name|PersistentState
operator|.
name|getState
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|firstLine
operator|=
name|ps
operator|.
name|firstLine
expr_stmt|;
name|offset
operator|=
name|ps
operator|.
name|offset
expr_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|checkForRotation
argument_list|()
expr_stmt|;
name|Environment
operator|.
name|logInfo
argument_list|(
literal|"Checked for rotation..."
argument_list|)
expr_stmt|;
name|reader
operator|.
name|skip
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|setNetworkProperties
argument_list|()
expr_stmt|;
block|}
DECL|method|setNetworkProperties ()
specifier|protected
name|void
name|setNetworkProperties
parameter_list|()
block|{
comment|// determine hostname and ip addresses for the node
try|try
block|{
comment|// Get hostname
name|hostname
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
expr_stmt|;
comment|// Get all associated ip addresses
name|ips
operator|=
name|InetAddress
operator|.
name|getAllByName
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Insert all EventRecords that can be extracted for    * the represented hardware component into a LocalStore.    *     * @param ls the LocalStore into which the EventRecords     * are to be stored.    */
DECL|method|monitor (LocalStore ls)
specifier|public
name|void
name|monitor
parameter_list|(
name|LocalStore
name|ls
parameter_list|)
block|{
name|int
name|in
init|=
literal|0
decl_stmt|;
name|EventRecord
name|er
init|=
literal|null
decl_stmt|;
name|Environment
operator|.
name|logInfo
argument_list|(
literal|"Started processing log..."
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|er
operator|=
name|getNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Environment.logInfo("Processing log line:\t" + in++);
if|if
condition|(
name|er
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|ls
operator|.
name|insert
argument_list|(
name|er
argument_list|)
expr_stmt|;
block|}
block|}
name|PersistentState
operator|.
name|updateState
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|firstLine
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|PersistentState
operator|.
name|writeState
argument_list|(
literal|"conf/parsing.state"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get an array of all EventRecords that can be extracted for    * the represented hardware component.    *     * @return The array of EventRecords    */
DECL|method|monitor ()
specifier|public
name|EventRecord
index|[]
name|monitor
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|EventRecord
argument_list|>
name|recs
init|=
operator|new
name|ArrayList
argument_list|<
name|EventRecord
argument_list|>
argument_list|()
decl_stmt|;
name|EventRecord
name|er
decl_stmt|;
while|while
condition|(
operator|(
name|er
operator|=
name|getNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|recs
operator|.
name|add
argument_list|(
name|er
argument_list|)
expr_stmt|;
name|EventRecord
index|[]
name|T
init|=
operator|new
name|EventRecord
index|[
name|recs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|recs
operator|.
name|toArray
argument_list|(
name|T
argument_list|)
return|;
block|}
comment|/**    * Continue parsing the log file until a valid log entry is identified.    * When one such entry is found, parse it and return a corresponding EventRecord.    *     *      * @return The EventRecord corresponding to the next log entry    */
DECL|method|getNext ()
specifier|public
name|EventRecord
name|getNext
parameter_list|()
block|{
try|try
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|firstLine
operator|==
literal|null
condition|)
name|firstLine
operator|=
operator|new
name|String
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|line
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
return|return
name|parseLine
argument_list|(
name|line
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Return the BufferedReader, that reads the log file    *      * @return The BufferedReader that reads the log file    */
DECL|method|getReader ()
specifier|public
name|BufferedReader
name|getReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
comment|/**    * Check whether the log file has been rotated. If so,    * start reading the file from the beginning.    *      */
DECL|method|checkForRotation ()
specifier|public
name|void
name|checkForRotation
parameter_list|()
block|{
try|try
block|{
name|BufferedReader
name|probe
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstLine
operator|==
literal|null
operator|||
operator|(
operator|!
name|firstLine
operator|.
name|equals
argument_list|(
name|probe
operator|.
name|readLine
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|probe
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// start reading the file from the beginning
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|firstLine
operator|=
literal|null
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Parses one line of the log. If the line contains a valid     * log entry, then an appropriate EventRecord is returned, after all    * relevant fields have been parsed.    *    *  @param line the log line to be parsed    *    *  @return the EventRecord representing the log entry of the line. If     *  the line does not contain a valid log entry, then the EventRecord     *  returned has isValid() = false. When the end-of-file has been reached,    *  null is returned to the caller.    */
DECL|method|parseLine (String line)
specifier|abstract
specifier|public
name|EventRecord
name|parseLine
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Parse a date found in Hadoop log file.    *     * @return a Calendar representing the date    */
DECL|method|parseDate (String strDate, String strTime)
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

