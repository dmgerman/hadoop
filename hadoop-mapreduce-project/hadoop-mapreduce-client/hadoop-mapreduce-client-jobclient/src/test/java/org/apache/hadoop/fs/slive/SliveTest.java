begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
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
name|DataInputStream
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
name|FileOutputStream
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|TreeMap
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|slive
operator|.
name|ArgumentParser
operator|.
name|ParsedOutput
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
name|util
operator|.
name|Tool
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
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * Slive test entry point + main program  *   * This program will output a help message given -help which can be used to  * determine the program options and configuration which will affect the program  * runtime. The program will take these options, either from configuration or  * command line and process them (and merge) and then establish a job which will  * thereafter run a set of mappers& reducers and then the output of the  * reduction will be reported on.  *   * The number of maps is specified by "slive.maps".  * The number of reduces is specified by "slive.reduces".  */
end_comment

begin_class
DECL|class|SliveTest
specifier|public
class|class
name|SliveTest
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SliveTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ensures the hdfs configurations are loaded if they exist
static|static
block|{
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-default.xml"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
literal|"hdfs-site.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|base
specifier|private
name|Configuration
name|base
decl_stmt|;
DECL|method|SliveTest (Configuration base)
specifier|public
name|SliveTest
parameter_list|(
name|Configuration
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|ParsedOutput
name|parsedOpts
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ArgumentParser
name|argHolder
init|=
operator|new
name|ArgumentParser
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|parsedOpts
operator|=
name|argHolder
operator|.
name|parse
argument_list|()
expr_stmt|;
if|if
condition|(
name|parsedOpts
operator|.
name|shouldOutputHelp
argument_list|()
condition|)
block|{
name|parsedOpts
operator|.
name|outputHelp
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to parse arguments due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running with option list "
operator|+
name|Helper
operator|.
name|stringifyArray
argument_list|(
name|args
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|ConfigExtractor
name|config
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ConfigMerger
name|cfgMerger
init|=
operator|new
name|ConfigMerger
argument_list|()
decl_stmt|;
name|Configuration
name|cfg
init|=
name|cfgMerger
operator|.
name|getMerged
argument_list|(
name|parsedOpts
argument_list|,
operator|new
name|Configuration
argument_list|(
name|base
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|config
operator|=
operator|new
name|ConfigExtractor
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to merge config due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to merge config& options!"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Options are:"
argument_list|)
expr_stmt|;
name|ConfigExtractor
operator|.
name|dumpOptions
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to dump options due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|boolean
name|jobOk
init|=
literal|false
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running job:"
argument_list|)
expr_stmt|;
name|runJob
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|jobOk
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to run job due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jobOk
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reporting on job:"
argument_list|)
expr_stmt|;
name|writeReport
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to report on job due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// attempt cleanup (not critical)
name|boolean
name|cleanUp
init|=
name|getBool
argument_list|(
name|parsedOpts
operator|.
name|getValue
argument_list|(
name|ConfigOption
operator|.
name|CLEANUP
operator|.
name|getOpt
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|cleanUp
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning up job:"
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to cleanup job due to error: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// all mostly worked
if|if
condition|(
name|jobOk
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// maybe didn't work
return|return
literal|1
return|;
block|}
comment|/**    * Checks if a string is a boolean or not and what type    *     * @param val    *          val to check    * @return boolean    */
DECL|method|getBool (String val)
specifier|private
name|boolean
name|getBool
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|cleanupOpt
init|=
name|val
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|cleanupOpt
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|cleanupOpt
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Sets up a job conf for the given job using the given config object. Ensures    * that the correct input format is set, the mapper and and reducer class and    * the input and output keys and value classes along with any other job    * configuration.    *     * @param config    * @return JobConf representing the job to be ran    * @throws IOException    */
DECL|method|getJob (ConfigExtractor config)
specifier|private
name|JobConf
name|getJob
parameter_list|(
name|ConfigExtractor
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|config
operator|.
name|getConfig
argument_list|()
argument_list|,
name|SliveTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|DummyInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|config
operator|.
name|getOutputPath
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|SliveMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setPartitionerClass
argument_list|(
name|SlivePartitioner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|SliveReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|TextOutputFormat
operator|.
name|setCompressOutput
argument_list|(
name|job
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|config
operator|.
name|getReducerAmount
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumMapTasks
argument_list|(
name|config
operator|.
name|getMapAmount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
comment|/**    * Runs the job given the provided config    *     * @param config    *          the config to run the job with    *     * @throws IOException    *           if can not run the given job    */
DECL|method|runJob (ConfigExtractor config)
specifier|private
name|void
name|runJob
parameter_list|(
name|ConfigExtractor
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|JobClient
operator|.
name|runJob
argument_list|(
name|getJob
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Attempts to write the report to the given output using the specified    * config. It will open up the expected reducer output file and read in its    * contents and then split up by operation output and sort by operation type    * and then for each operation type it will generate a report to the specified    * result file and the console.    *     * @param cfg    *          the config specifying the files and output    *     * @throws Exception    *           if files can not be opened/closed/read or invalid format    */
DECL|method|writeReport (ConfigExtractor cfg)
specifier|private
name|void
name|writeReport
parameter_list|(
name|ConfigExtractor
name|cfg
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|dn
init|=
name|cfg
operator|.
name|getOutputPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Writing report using contents of "
operator|+
name|dn
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dn
operator|.
name|getFileSystem
argument_list|(
name|cfg
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|reduceFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|BufferedReader
name|fileReader
init|=
literal|null
decl_stmt|;
name|PrintWriter
name|reportWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|noOperations
init|=
operator|new
name|ArrayList
argument_list|<
name|OperationOutput
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|OperationOutput
argument_list|>
argument_list|>
name|splitTypes
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|OperationOutput
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FileStatus
name|fn
range|:
name|reduceFiles
control|)
block|{
if|if
condition|(
operator|!
name|fn
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"part"
argument_list|)
condition|)
continue|continue;
name|fileReader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|fn
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|fileReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|pieces
index|[]
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|pieces
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|OperationOutput
name|data
init|=
operator|new
name|OperationOutput
argument_list|(
name|pieces
index|[
literal|0
index|]
argument_list|,
name|pieces
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|String
name|op
init|=
operator|(
name|data
operator|.
name|getOperationType
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|OperationOutput
argument_list|>
name|opList
init|=
name|splitTypes
operator|.
name|get
argument_list|(
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|opList
operator|==
literal|null
condition|)
block|{
name|opList
operator|=
operator|new
name|ArrayList
argument_list|<
name|OperationOutput
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|opList
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|splitTypes
operator|.
name|put
argument_list|(
name|op
argument_list|,
name|opList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|noOperations
operator|.
name|add
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unparseable line "
operator|+
name|line
argument_list|)
throw|;
block|}
block|}
name|fileReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|fileReader
operator|=
literal|null
expr_stmt|;
block|}
name|File
name|resFile
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cfg
operator|.
name|getResultFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|resFile
operator|=
operator|new
name|File
argument_list|(
name|cfg
operator|.
name|getResultFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resFile
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Report results being placed to logging output and to file "
operator|+
name|resFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|reportWriter
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|resFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Report results being placed to logging output"
argument_list|)
expr_stmt|;
block|}
name|ReportWriter
name|reporter
init|=
operator|new
name|ReportWriter
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|noOperations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|reporter
operator|.
name|basicReport
argument_list|(
name|noOperations
argument_list|,
name|reportWriter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|opType
range|:
name|splitTypes
operator|.
name|keySet
argument_list|()
control|)
block|{
name|reporter
operator|.
name|opReport
argument_list|(
name|opType
argument_list|,
name|splitTypes
operator|.
name|get
argument_list|(
name|opType
argument_list|)
argument_list|,
name|reportWriter
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|fileReader
operator|!=
literal|null
condition|)
block|{
name|fileReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reportWriter
operator|!=
literal|null
condition|)
block|{
name|reportWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Cleans up the base directory by removing it    *     * @param cfg    *          ConfigExtractor which has location of base directory    *     * @throws IOException    */
DECL|method|cleanup (ConfigExtractor cfg)
specifier|private
name|void
name|cleanup
parameter_list|(
name|ConfigExtractor
name|cfg
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|base
init|=
name|cfg
operator|.
name|getBaseDirectory
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to recursively delete "
operator|+
name|base
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|base
operator|.
name|getFileSystem
argument_list|(
name|cfg
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|base
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The main program entry point. Sets up and parses the command line options,    * then merges those options and then dumps those options and the runs the    * corresponding map/reduce job that those operations represent and then    * writes the report for the output of the run that occurred.    *     * @param args    *          command line options    */
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
throws|throws
name|Exception
block|{
name|Configuration
name|startCfg
init|=
operator|new
name|Configuration
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|SliveTest
name|runner
init|=
operator|new
name|SliveTest
argument_list|(
name|startCfg
argument_list|)
decl_stmt|;
name|int
name|ec
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|runner
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|ec
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// Configurable
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|base
return|;
block|}
annotation|@
name|Override
comment|// Configurable
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|conf
expr_stmt|;
block|}
block|}
end_class

end_unit

