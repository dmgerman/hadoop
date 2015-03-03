begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|Configurable
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
name|conf
operator|.
name|Configuration
operator|.
name|DeprecationDelta
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|LongWritable
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
name|WritableComparable
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
name|WritableComparator
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
name|mapred
operator|.
name|FileInputFormat
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
name|mapred
operator|.
name|FileOutputFormat
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
name|mapred
operator|.
name|JobClient
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|MapReduceBase
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
name|mapred
operator|.
name|Mapper
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
name|mapred
operator|.
name|OutputCollector
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
name|mapred
operator|.
name|Reporter
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
name|mapred
operator|.
name|TextInputFormat
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
name|mapred
operator|.
name|TextOutputFormat
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
name|mapred
operator|.
name|lib
operator|.
name|LongSumReducer
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
name|mapreduce
operator|.
name|lib
operator|.
name|map
operator|.
name|RegexMapper
import|;
end_import

begin_comment
comment|/**  * Logalyzer: A utility tool for archiving and analyzing hadoop logs.  *<p>  * This tool supports archiving and anaylzing (sort/grep) of log-files.  * It takes as input  *  a) Input uri which will serve uris of the logs to be archived.  *  b) Output directory (not mandatory).  *  b) Directory on dfs to archive the logs.   *  c) The sort/grep patterns for analyzing the files and separator for boundaries.  * Usage:   * Logalyzer -archive -archiveDir&lt;directory to archive logs&gt; -analysis  *&lt;directory&gt; -logs&lt;log-list uri&gt; -grep&lt;pattern&gt; -sort  *&lt;col1, col2&gt; -separator&lt;separator&gt;  *<p>  */
end_comment

begin_class
DECL|class|Logalyzer
specifier|public
class|class
name|Logalyzer
block|{
comment|// Constants
DECL|field|fsConfig
specifier|private
specifier|static
name|Configuration
name|fsConfig
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|SORT_COLUMNS
specifier|public
specifier|static
specifier|final
name|String
name|SORT_COLUMNS
init|=
literal|"logalizer.logcomparator.sort.columns"
decl_stmt|;
DECL|field|COLUMN_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|COLUMN_SEPARATOR
init|=
literal|"logalizer.logcomparator.column.separator"
decl_stmt|;
static|static
block|{
name|Configuration
operator|.
name|addDeprecations
argument_list|(
operator|new
name|DeprecationDelta
index|[]
block|{
operator|new
name|DeprecationDelta
argument_list|(
literal|"mapred.reducer.sort"
argument_list|,
name|SORT_COLUMNS
argument_list|)
block|,
operator|new
name|DeprecationDelta
argument_list|(
literal|"mapred.reducer.separator"
argument_list|,
name|COLUMN_SEPARATOR
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** A {@link Mapper} that extracts text matching a regular expression. */
DECL|class|LogRegexMapper
specifier|public
specifier|static
class|class
name|LogRegexMapper
parameter_list|<
name|K
extends|extends
name|WritableComparable
parameter_list|>
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|K
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|>
block|{
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|job
operator|.
name|get
argument_list|(
name|RegexMapper
operator|.
name|PATTERN
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|map (K key, Text value, OutputCollector<Text, LongWritable> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|K
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|LongWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|text
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|output
operator|.
name|collect
argument_list|(
name|value
argument_list|,
operator|new
name|LongWritable
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** A WritableComparator optimized for UTF8 keys of the logs. */
DECL|class|LogComparator
specifier|public
specifier|static
class|class
name|LogComparator
extends|extends
name|Text
operator|.
name|Comparator
implements|implements
name|Configurable
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Logalyzer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|JobConf
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|sortSpec
specifier|private
name|String
index|[]
name|sortSpec
init|=
literal|null
decl_stmt|;
DECL|field|columnSeparator
specifier|private
name|String
name|columnSeparator
init|=
literal|null
decl_stmt|;
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|instanceof
name|JobConf
condition|)
block|{
name|this
operator|.
name|conf
operator|=
operator|(
name|JobConf
operator|)
name|conf
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|//Initialize the specification for *comparision*
name|String
name|sortColumns
init|=
name|this
operator|.
name|conf
operator|.
name|get
argument_list|(
name|SORT_COLUMNS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortColumns
operator|!=
literal|null
condition|)
block|{
name|sortSpec
operator|=
name|sortColumns
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
comment|//Column-separator
name|columnSeparator
operator|=
name|this
operator|.
name|conf
operator|.
name|get
argument_list|(
name|COLUMN_SEPARATOR
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
if|if
condition|(
name|sortSpec
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|compare
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
return|;
block|}
try|try
block|{
name|Text
name|logline1
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|logline1
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line1
init|=
name|logline1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
index|[]
name|logColumns1
init|=
name|line1
operator|.
name|split
argument_list|(
name|columnSeparator
argument_list|)
decl_stmt|;
name|Text
name|logline2
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|logline2
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line2
init|=
name|logline2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
index|[]
name|logColumns2
init|=
name|line2
operator|.
name|split
argument_list|(
name|columnSeparator
argument_list|)
decl_stmt|;
if|if
condition|(
name|logColumns1
operator|==
literal|null
operator|||
name|logColumns2
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|compare
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
return|;
block|}
comment|//Compare column-wise according to *sortSpec*
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sortSpec
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|int
name|column
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sortSpec
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|String
name|c1
init|=
name|logColumns1
index|[
name|column
index|]
decl_stmt|;
name|String
name|c2
init|=
name|logColumns2
index|[
name|column
index|]
decl_stmt|;
comment|//Compare columns
name|int
name|comparision
init|=
name|super
operator|.
name|compareBytes
argument_list|(
name|c1
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|c1
operator|.
name|length
argument_list|()
argument_list|,
name|c2
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|c2
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|//They differ!
if|if
condition|(
name|comparision
operator|!=
literal|0
condition|)
block|{
return|return
name|comparision
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Caught "
operator|+
name|ioe
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
return|return
literal|0
return|;
block|}
static|static
block|{
comment|// register this comparator
name|WritableComparator
operator|.
name|define
argument_list|(
name|Text
operator|.
name|class
argument_list|,
operator|new
name|LogComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * doArchive: Workhorse function to archive log-files.    * @param logListURI : The uri which will serve list of log-files to archive.    * @param archiveDirectory : The directory to store archived logfiles.    * @throws IOException    */
specifier|public
name|void
DECL|method|doArchive (String logListURI, String archiveDirectory)
name|doArchive
parameter_list|(
name|String
name|logListURI
parameter_list|,
name|String
name|archiveDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|destURL
init|=
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|fsConfig
argument_list|)
operator|+
name|archiveDirectory
decl_stmt|;
name|DistCpV1
operator|.
name|copy
argument_list|(
operator|new
name|JobConf
argument_list|(
name|fsConfig
argument_list|)
argument_list|,
name|logListURI
argument_list|,
name|destURL
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * doAnalyze:     * @param inputFilesDirectory : Directory containing the files to be analyzed.    * @param outputDirectory : Directory to store analysis (output).    * @param grepPattern : Pattern to *grep* for.    * @param sortColumns : Sort specification for output.    * @param columnSeparator : Column separator.    * @throws IOException    */
specifier|public
name|void
DECL|method|doAnalyze (String inputFilesDirectory, String outputDirectory, String grepPattern, String sortColumns, String columnSeparator)
name|doAnalyze
parameter_list|(
name|String
name|inputFilesDirectory
parameter_list|,
name|String
name|outputDirectory
parameter_list|,
name|String
name|grepPattern
parameter_list|,
name|String
name|sortColumns
parameter_list|,
name|String
name|columnSeparator
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|grepInput
init|=
operator|new
name|Path
argument_list|(
name|inputFilesDirectory
argument_list|)
decl_stmt|;
name|Path
name|analysisOutput
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|outputDirectory
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|analysisOutput
operator|=
operator|new
name|Path
argument_list|(
name|inputFilesDirectory
argument_list|,
literal|"logalyzer_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analysisOutput
operator|=
operator|new
name|Path
argument_list|(
name|outputDirectory
argument_list|)
expr_stmt|;
block|}
name|JobConf
name|grepJob
init|=
operator|new
name|JobConf
argument_list|(
name|fsConfig
argument_list|)
decl_stmt|;
name|grepJob
operator|.
name|setJobName
argument_list|(
literal|"logalyzer-grep-sort"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|grepJob
argument_list|,
name|grepInput
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setMapperClass
argument_list|(
name|LogRegexMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|set
argument_list|(
name|RegexMapper
operator|.
name|PATTERN
argument_list|,
name|grepPattern
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|set
argument_list|(
name|SORT_COLUMNS
argument_list|,
name|sortColumns
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|set
argument_list|(
name|COLUMN_SEPARATOR
argument_list|,
name|columnSeparator
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setCombinerClass
argument_list|(
name|LongSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setReducerClass
argument_list|(
name|LongSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|grepJob
argument_list|,
name|analysisOutput
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setOutputValueClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setOutputKeyComparatorClass
argument_list|(
name|LogComparator
operator|.
name|class
argument_list|)
expr_stmt|;
name|grepJob
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// write a single file
name|JobClient
operator|.
name|runJob
argument_list|(
name|grepJob
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|Logalyzer
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|version
init|=
literal|"Logalyzer.0.0.1"
decl_stmt|;
name|String
name|usage
init|=
literal|"Usage: Logalyzer [-archive -logs<urlsFile>] "
operator|+
literal|"-archiveDir<archiveDirectory> "
operator|+
literal|"-grep<pattern> -sort<column1,column2,...> -separator<separator> "
operator|+
literal|"-analysis<outputDirectory>"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//Command line arguments
name|boolean
name|archive
init|=
literal|false
decl_stmt|;
name|boolean
name|grep
init|=
literal|false
decl_stmt|;
name|boolean
name|sort
init|=
literal|false
decl_stmt|;
name|String
name|archiveDir
init|=
literal|""
decl_stmt|;
name|String
name|logListURI
init|=
literal|""
decl_stmt|;
name|String
name|grepPattern
init|=
literal|".*"
decl_stmt|;
name|String
name|sortColumns
init|=
literal|""
decl_stmt|;
name|String
name|columnSeparator
init|=
literal|" "
decl_stmt|;
name|String
name|outputDirectory
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-archive"
argument_list|)
condition|)
block|{
name|archive
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-archiveDir"
argument_list|)
condition|)
block|{
name|archiveDir
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-grep"
argument_list|)
condition|)
block|{
name|grep
operator|=
literal|true
expr_stmt|;
name|grepPattern
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-logs"
argument_list|)
condition|)
block|{
name|logListURI
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-sort"
argument_list|)
condition|)
block|{
name|sort
operator|=
literal|true
expr_stmt|;
name|sortColumns
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-separator"
argument_list|)
condition|)
block|{
name|columnSeparator
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-analysis"
argument_list|)
condition|)
block|{
name|outputDirectory
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"analysisDir = "
operator|+
name|outputDirectory
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"archiveDir = "
operator|+
name|archiveDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"logListURI = "
operator|+
name|logListURI
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"grepPattern = "
operator|+
name|grepPattern
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sortColumns = "
operator|+
name|sortColumns
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"separator = "
operator|+
name|columnSeparator
argument_list|)
expr_stmt|;
try|try
block|{
name|Logalyzer
name|logalyzer
init|=
operator|new
name|Logalyzer
argument_list|()
decl_stmt|;
comment|// Archive?
if|if
condition|(
name|archive
condition|)
block|{
name|logalyzer
operator|.
name|doArchive
argument_list|(
name|logListURI
argument_list|,
name|archiveDir
argument_list|)
expr_stmt|;
block|}
comment|// Analyze?
if|if
condition|(
name|grep
operator|||
name|sort
condition|)
block|{
name|logalyzer
operator|.
name|doAnalyze
argument_list|(
name|archiveDir
argument_list|,
name|outputDirectory
argument_list|,
name|grepPattern
argument_list|,
name|sortColumns
argument_list|,
name|columnSeparator
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|//main
block|}
end_class

begin_comment
comment|//class Logalyzer
end_comment

end_unit

