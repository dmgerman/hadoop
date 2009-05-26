begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|java
operator|.
name|net
operator|.
name|URLClassLoader
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Option
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
name|cli
operator|.
name|OptionBuilder
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
name|cli
operator|.
name|Options
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
name|cli
operator|.
name|ParseException
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

begin_comment
comment|/**  *<code>GenericOptionsParser</code> is a utility to parse command line  * arguments generic to the Hadoop framework.   *   *<code>GenericOptionsParser</code> recognizes several standarad command   * line arguments, enabling applications to easily specify a namenode, a   * jobtracker, additional configuration resources etc.  *   *<h4 id="GenericOptions">Generic Options</h4>  *   *<p>The supported generic options are:</p>  *<p><blockquote><pre>  *     -conf&lt;configuration file&gt;     specify a configuration file  *     -D&lt;property=value&gt;            use value for given property  *     -fs&lt;local|namenode:port&gt;      specify a namenode  *     -jt&lt;local|jobtracker:port&gt;    specify a job tracker  *     -files&lt;comma separated list of files&gt;    specify comma separated  *                            files to be copied to the map reduce cluster  *     -libjars&lt;comma separated list of jars&gt;   specify comma separated  *                            jar files to include in the classpath.  *     -archives&lt;comma separated list of archives&gt;    specify comma  *             separated archives to be unarchived on the compute machines.   *</pre></blockquote></p>  *   *<p>The general command line syntax is:</p>  *<p><tt><pre>  * bin/hadoop command [genericOptions] [commandOptions]  *</pre></tt></p>  *   *<p>Generic command line arguments<strong>might</strong> modify   *<code>Configuration</code> objects, given to constructors.</p>  *   *<p>The functionality is implemented using Commons CLI.</p>  *  *<p>Examples:</p>  *<p><blockquote><pre>  * $ bin/hadoop dfs -fs darwin:8020 -ls /data  * list /data directory in dfs with namenode darwin:8020  *   * $ bin/hadoop dfs -D fs.default.name=darwin:8020 -ls /data  * list /data directory in dfs with namenode darwin:8020  *       * $ bin/hadoop dfs -conf hadoop-site.xml -ls /data  * list /data directory in dfs with conf specified in hadoop-site.xml  *       * $ bin/hadoop job -D mapred.job.tracker=darwin:50020 -submit job.xml  * submit a job to job tracker darwin:50020  *       * $ bin/hadoop job -jt darwin:50020 -submit job.xml  * submit a job to job tracker darwin:50020  *       * $ bin/hadoop job -jt local -submit job.xml  * submit a job to local runner  *   * $ bin/hadoop jar -libjars testlib.jar   * -archives test.tgz -files file.txt inputjar args  * job submission with libjars, files and archives  *</pre></blockquote></p>  *  * @see Tool  * @see ToolRunner  */
end_comment

begin_class
DECL|class|GenericOptionsParser
specifier|public
class|class
name|GenericOptionsParser
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
name|GenericOptionsParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|commandLine
specifier|private
name|CommandLine
name|commandLine
decl_stmt|;
comment|/**    * Create an options parser with the given options to parse the args.    * @param opts the options    * @param args the command line arguments    */
DECL|method|GenericOptionsParser (Options opts, String[] args)
specifier|public
name|GenericOptionsParser
parameter_list|(
name|Options
name|opts
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|Options
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an options parser to parse the args.    * @param args the command line arguments    */
DECL|method|GenericOptionsParser (String[] args)
specifier|public
name|GenericOptionsParser
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|Options
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a<code>GenericOptionsParser<code> to parse only the generic Hadoop      * arguments.     *     * The array of string arguments other than the generic arguments can be     * obtained by {@link #getRemainingArgs()}.    *     * @param conf the<code>Configuration</code> to modify.    * @param args command-line arguments.    */
DECL|method|GenericOptionsParser (Configuration conf, String[] args)
specifier|public
name|GenericOptionsParser
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
operator|new
name|Options
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a<code>GenericOptionsParser</code> to parse given options as well     * as generic Hadoop options.     *     * The resulting<code>CommandLine</code> object can be obtained by     * {@link #getCommandLine()}.    *     * @param conf the configuration to modify      * @param options options built by the caller     * @param args User-specified arguments    */
DECL|method|GenericOptionsParser (Configuration conf, Options options, String[] args)
specifier|public
name|GenericOptionsParser
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Options
name|options
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|parseGeneralOptions
argument_list|(
name|options
argument_list|,
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Returns an array of Strings containing only application-specific arguments.    *     * @return array of<code>String</code>s containing the un-parsed arguments    * or<strong>empty array</strong> if commandLine was not defined.    */
DECL|method|getRemainingArgs ()
specifier|public
name|String
index|[]
name|getRemainingArgs
parameter_list|()
block|{
return|return
operator|(
name|commandLine
operator|==
literal|null
operator|)
condition|?
operator|new
name|String
index|[]
block|{}
else|:
name|commandLine
operator|.
name|getArgs
argument_list|()
return|;
block|}
comment|/**    * Get the modified configuration    * @return the configuration that has the modified parameters.    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Returns the commons-cli<code>CommandLine</code> object     * to process the parsed arguments.     *     * Note: If the object is created with     * {@link #GenericOptionsParser(Configuration, String[])}, then returned     * object will only contain parsed generic options.    *     * @return<code>CommandLine</code> representing list of arguments     *         parsed against Options descriptor.    */
DECL|method|getCommandLine ()
specifier|public
name|CommandLine
name|getCommandLine
parameter_list|()
block|{
return|return
name|commandLine
return|;
block|}
comment|/**    * Specify properties of each generic option    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"static-access"
argument_list|)
DECL|method|buildGeneralOptions (Options opts)
specifier|private
specifier|static
name|Options
name|buildGeneralOptions
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
name|Option
name|fs
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"local|namenode:port"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify a namenode"
argument_list|)
operator|.
name|create
argument_list|(
literal|"fs"
argument_list|)
decl_stmt|;
name|Option
name|jt
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"local|jobtracker:port"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify a job tracker"
argument_list|)
operator|.
name|create
argument_list|(
literal|"jt"
argument_list|)
decl_stmt|;
name|Option
name|oconf
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"configuration file"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify an application configuration file"
argument_list|)
operator|.
name|create
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
name|Option
name|property
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"property=value"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"use value for given property"
argument_list|)
operator|.
name|create
argument_list|(
literal|'D'
argument_list|)
decl_stmt|;
name|Option
name|libjars
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"paths"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"comma separated jar files to include in the classpath."
argument_list|)
operator|.
name|create
argument_list|(
literal|"libjars"
argument_list|)
decl_stmt|;
name|Option
name|files
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"paths"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"comma separated files to be copied to the "
operator|+
literal|"map reduce cluster"
argument_list|)
operator|.
name|create
argument_list|(
literal|"files"
argument_list|)
decl_stmt|;
name|Option
name|archives
init|=
name|OptionBuilder
operator|.
name|withArgName
argument_list|(
literal|"paths"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"comma separated archives to be unarchived"
operator|+
literal|" on the compute machines."
argument_list|)
operator|.
name|create
argument_list|(
literal|"archives"
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|jt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|oconf
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|libjars
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|archives
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
comment|/**    * Modify configuration according user-specified generic options    * @param conf Configuration to be modified    * @param line User-specified generic options    */
DECL|method|processGeneralOptions (Configuration conf, CommandLine line)
specifier|private
name|void
name|processGeneralOptions
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|CommandLine
name|line
parameter_list|)
block|{
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"fs"
argument_list|)
condition|)
block|{
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"fs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"jt"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"mapred.job.tracker"
argument_list|,
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"jt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"conf"
argument_list|)
condition|)
block|{
name|String
index|[]
name|values
init|=
name|line
operator|.
name|getOptionValues
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|conf
operator|.
name|addResource
argument_list|(
operator|new
name|Path
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"libjars"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"tmpjars"
argument_list|,
name|validateFiles
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"libjars"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
comment|//setting libjars in client classpath
name|URL
index|[]
name|libjars
init|=
name|getLibJars
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|libjars
operator|!=
literal|null
operator|&&
name|libjars
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|conf
operator|.
name|setClassLoader
argument_list|(
operator|new
name|URLClassLoader
argument_list|(
name|libjars
argument_list|,
name|conf
operator|.
name|getClassLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
operator|new
name|URLClassLoader
argument_list|(
name|libjars
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"files"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"tmpfiles"
argument_list|,
name|validateFiles
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"files"
argument_list|)
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"archives"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"tmparchives"
argument_list|,
name|validateFiles
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"archives"
argument_list|)
argument_list|,
name|conf
argument_list|)
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'D'
argument_list|)
condition|)
block|{
name|String
index|[]
name|property
init|=
name|line
operator|.
name|getOptionValues
argument_list|(
literal|'D'
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|property
control|)
block|{
name|String
index|[]
name|keyval
init|=
name|prop
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyval
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|keyval
index|[
literal|0
index|]
argument_list|,
name|keyval
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"mapred.used.genericoptionsparser"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * If libjars are set in the conf, parse the libjars.    * @param conf    * @return libjar urls    * @throws IOException    */
DECL|method|getLibJars (Configuration conf)
specifier|public
specifier|static
name|URL
index|[]
name|getLibJars
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|jars
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"tmpjars"
argument_list|)
decl_stmt|;
if|if
condition|(
name|jars
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
name|files
init|=
name|jars
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|URL
index|[]
name|cp
init|=
operator|new
name|URL
index|[
name|files
operator|.
name|length
index|]
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
name|cp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Path
name|tmp
init|=
operator|new
name|Path
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|cp
index|[
name|i
index|]
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|pathToFile
argument_list|(
name|tmp
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
expr_stmt|;
block|}
return|return
name|cp
return|;
block|}
comment|/**    * takes input as a comma separated list of files    * and verifies if they exist. It defaults for file:///    * if the files specified do not have a scheme.    * it returns the paths uri converted defaulting to file:///.    * So an input of  /home/user/file1,/home/user/file2 would return    * file:///home/user/file1,file:///home/user/file2    * @param files    * @return    */
DECL|method|validateFiles (String files, Configuration conf)
specifier|private
name|String
name|validateFiles
parameter_list|(
name|String
name|files
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|fileArr
init|=
name|files
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|String
index|[]
name|finalArr
init|=
operator|new
name|String
index|[
name|fileArr
operator|.
name|length
index|]
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
name|fileArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|tmp
init|=
name|fileArr
index|[
name|i
index|]
decl_stmt|;
name|String
name|finalPath
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
name|URI
name|pathURI
init|=
name|path
operator|.
name|toUri
argument_list|()
decl_stmt|;
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathURI
operator|.
name|getScheme
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|//default to the local file system
comment|//check if the file exists or not first
if|if
condition|(
operator|!
name|localFs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|tmp
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|finalPath
operator|=
name|path
operator|.
name|makeQualified
argument_list|(
name|localFs
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// check if the file exists in this file system
comment|// we need to recreate this filesystem object to copy
comment|// these files to the file system jobtracker is running
comment|// on.
name|FileSystem
name|fs
init|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|tmp
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|finalPath
operator|=
name|path
operator|.
name|makeQualified
argument_list|(
name|fs
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{}
empty_stmt|;
block|}
name|finalArr
index|[
name|i
index|]
operator|=
name|finalPath
expr_stmt|;
block|}
return|return
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|finalArr
argument_list|)
return|;
block|}
comment|/**    * Parse the user-specified options, get the generic options, and modify    * configuration accordingly    * @param conf Configuration to be modified    * @param args User-specified arguments    * @return Command-specific arguments    */
DECL|method|parseGeneralOptions (Options opts, Configuration conf, String[] args)
specifier|private
name|String
index|[]
name|parseGeneralOptions
parameter_list|(
name|Options
name|opts
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
block|{
name|opts
operator|=
name|buildGeneralOptions
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
try|try
block|{
name|commandLine
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|processGeneralOptions
argument_list|(
name|conf
argument_list|,
name|commandLine
argument_list|)
expr_stmt|;
return|return
name|commandLine
operator|.
name|getArgs
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"options parsing failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|"general options are: "
argument_list|,
name|opts
argument_list|)
expr_stmt|;
block|}
return|return
name|args
return|;
block|}
comment|/**    * Print the usage message for generic command-line options supported.    *     * @param out stream to print the usage message to.    */
DECL|method|printGenericCommandUsage (PrintStream out)
specifier|public
specifier|static
name|void
name|printGenericCommandUsage
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Generic options supported are"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-conf<configuration file>     specify an application configuration file"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-D<property=value>            use value for given property"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-fs<local|namenode:port>      specify a namenode"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-jt<local|jobtracker:port>    specify a job tracker"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-files<comma separated list of files>    "
operator|+
literal|"specify comma separated files to be copied to the map reduce cluster"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-libjars<comma separated list of jars>    "
operator|+
literal|"specify comma separated jar files to include in the classpath."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-archives<comma separated list of archives>    "
operator|+
literal|"specify comma separated archives to be unarchived"
operator|+
literal|" on the compute machines.\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"The general command line syntax is"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"bin/hadoop command [genericOptions] [commandOptions]\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

