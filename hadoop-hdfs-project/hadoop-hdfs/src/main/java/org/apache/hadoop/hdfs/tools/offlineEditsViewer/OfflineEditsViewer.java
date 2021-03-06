begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|Configured
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
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
operator|.
name|OfflineEditsLoader
operator|.
name|OfflineEditsLoaderFactory
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
name|cli
operator|.
name|PosixParser
import|;
end_import

begin_comment
comment|/**  * This class implements an offline edits viewer, tool that  * can be used to view edit logs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OfflineEditsViewer
specifier|public
class|class
name|OfflineEditsViewer
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|HELP_OPT
specifier|private
specifier|static
specifier|final
name|String
name|HELP_OPT
init|=
literal|"-h"
decl_stmt|;
DECL|field|HELP_LONGOPT
specifier|private
specifier|static
specifier|final
name|String
name|HELP_LONGOPT
init|=
literal|"--help"
decl_stmt|;
DECL|field|defaultProcessor
specifier|private
specifier|final
specifier|static
name|String
name|defaultProcessor
init|=
literal|"xml"
decl_stmt|;
comment|/**    * Print help.    */
DECL|method|printHelp ()
specifier|private
name|void
name|printHelp
parameter_list|()
block|{
name|String
name|summary
init|=
literal|"Usage: bin/hdfs oev [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE\n"
operator|+
literal|"Offline edits viewer\n"
operator|+
literal|"Parse a Hadoop edits log file INPUT_FILE and save results\n"
operator|+
literal|"in OUTPUT_FILE.\n"
operator|+
literal|"Required command line arguments:\n"
operator|+
literal|"-i,--inputFile<arg>   edits file to process, xml (case\n"
operator|+
literal|"                       insensitive) extension means XML format,\n"
operator|+
literal|"                       any other filename means binary format.\n"
operator|+
literal|"                       XML/Binary format input file is not allowed\n"
operator|+
literal|"                       to be processed by the same type processor.\n"
operator|+
literal|"-o,--outputFile<arg>  Name of output file. If the specified\n"
operator|+
literal|"                       file exists, it will be overwritten,\n"
operator|+
literal|"                       format of the file is determined\n"
operator|+
literal|"                       by -p option\n"
operator|+
literal|"\n"
operator|+
literal|"Optional command line arguments:\n"
operator|+
literal|"-p,--processor<arg>   Select which type of processor to apply\n"
operator|+
literal|"                       against image file, currently supported\n"
operator|+
literal|"                       processors are: binary (native binary format\n"
operator|+
literal|"                       that Hadoop uses), xml (default, XML\n"
operator|+
literal|"                       format), stats (prints statistics about\n"
operator|+
literal|"                       edits file)\n"
operator|+
literal|"-h,--help              Display usage information and exit\n"
operator|+
literal|"-f,--fix-txids         Renumber the transaction IDs in the input,\n"
operator|+
literal|"                       so that there are no gaps or invalid\n"
operator|+
literal|"                       transaction IDs.\n"
operator|+
literal|"-r,--recover           When reading binary edit logs, use recovery \n"
operator|+
literal|"                       mode.  This will give you the chance to skip \n"
operator|+
literal|"                       corrupt parts of the edit log.\n"
operator|+
literal|"-v,--verbose           More verbose output, prints the input and\n"
operator|+
literal|"                       output filenames, for processors that write\n"
operator|+
literal|"                       to a file, also output to screen. On large\n"
operator|+
literal|"                       image files this will dramatically increase\n"
operator|+
literal|"                       processing time (default is false).\n"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|summary
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build command-line options and descriptions    *    * @return command line options    */
DECL|method|buildOptions ()
specifier|public
specifier|static
name|Options
name|buildOptions
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
comment|// Build in/output file arguments, which are required, but there is no
comment|// addOption method that can specify this
name|OptionBuilder
operator|.
name|isRequired
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArgs
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"outputFilename"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"o"
argument_list|)
argument_list|)
expr_stmt|;
name|OptionBuilder
operator|.
name|isRequired
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|hasArgs
argument_list|()
expr_stmt|;
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"inputFilename"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|create
argument_list|(
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"p"
argument_list|,
literal|"processor"
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"v"
argument_list|,
literal|"verbose"
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"f"
argument_list|,
literal|"fix-txids"
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"r"
argument_list|,
literal|"recover"
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
comment|/** Process an edit log using the chosen processor or visitor.    *     * @param inputFileName   The file to process    * @param outputFileName  The output file name    * @param processor       If visitor is null, the processor to use    * @param visitor         If non-null, the visitor to use.    *     * @return                0 on success; error code otherwise    */
DECL|method|go (String inputFileName, String outputFileName, String processor, Flags flags, OfflineEditsVisitor visitor)
specifier|public
name|int
name|go
parameter_list|(
name|String
name|inputFileName
parameter_list|,
name|String
name|outputFileName
parameter_list|,
name|String
name|processor
parameter_list|,
name|Flags
name|flags
parameter_list|,
name|OfflineEditsVisitor
name|visitor
parameter_list|)
block|{
if|if
condition|(
name|flags
operator|.
name|getPrintToScreen
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"input  ["
operator|+
name|inputFileName
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"output ["
operator|+
name|outputFileName
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|boolean
name|xmlInput
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|inputFileName
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmlInput
operator|&&
name|StringUtils
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"xml"
argument_list|,
name|processor
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"XML format input file is not allowed"
operator|+
literal|" to be processed by XML processor."
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|xmlInput
operator|&&
name|StringUtils
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"binary"
argument_list|,
name|processor
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Binary format input file is not allowed"
operator|+
literal|" to be processed by Binary processor."
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
if|if
condition|(
name|visitor
operator|==
literal|null
condition|)
block|{
name|visitor
operator|=
name|OfflineEditsVisitorFactory
operator|.
name|getEditsVisitor
argument_list|(
name|outputFileName
argument_list|,
name|processor
argument_list|,
name|flags
operator|.
name|getPrintToScreen
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|OfflineEditsLoader
name|loader
init|=
name|OfflineEditsLoaderFactory
operator|.
name|createLoader
argument_list|(
name|visitor
argument_list|,
name|inputFileName
argument_list|,
name|xmlInput
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|loader
operator|.
name|loadEdits
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Encountered exception. Exiting: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|class|Flags
specifier|public
specifier|static
class|class
name|Flags
block|{
DECL|field|printToScreen
specifier|private
name|boolean
name|printToScreen
init|=
literal|false
decl_stmt|;
DECL|field|fixTxIds
specifier|private
name|boolean
name|fixTxIds
init|=
literal|false
decl_stmt|;
DECL|field|recoveryMode
specifier|private
name|boolean
name|recoveryMode
init|=
literal|false
decl_stmt|;
DECL|method|Flags ()
specifier|public
name|Flags
parameter_list|()
block|{     }
DECL|method|getPrintToScreen ()
specifier|public
name|boolean
name|getPrintToScreen
parameter_list|()
block|{
return|return
name|printToScreen
return|;
block|}
DECL|method|setPrintToScreen ()
specifier|public
name|void
name|setPrintToScreen
parameter_list|()
block|{
name|printToScreen
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getFixTxIds ()
specifier|public
name|boolean
name|getFixTxIds
parameter_list|()
block|{
return|return
name|fixTxIds
return|;
block|}
DECL|method|setFixTxIds ()
specifier|public
name|void
name|setFixTxIds
parameter_list|()
block|{
name|fixTxIds
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getRecoveryMode ()
specifier|public
name|boolean
name|getRecoveryMode
parameter_list|()
block|{
return|return
name|recoveryMode
return|;
block|}
DECL|method|setRecoveryMode ()
specifier|public
name|void
name|setRecoveryMode
parameter_list|()
block|{
name|recoveryMode
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**    * Main entry point for ToolRunner (see ToolRunner docs)    *    * @param argv The parameters passed to this program.    * @return 0 on success, non zero on error.    */
annotation|@
name|Override
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|options
init|=
name|buildOptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|argv
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|// print help and exit with zero exit code
if|if
condition|(
name|argv
operator|.
name|length
operator|==
literal|1
operator|&&
name|isHelpOption
argument_list|(
name|argv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|printHelp
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
name|CommandLineParser
name|parser
init|=
operator|new
name|PosixParser
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
decl_stmt|;
try|try
block|{
name|cmd
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Error parsing command-line options: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|printHelp
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
comment|// print help and exit with non zero exit code since
comment|// it is not expected to give help and other options together.
name|printHelp
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|String
name|inputFileName
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"i"
argument_list|)
decl_stmt|;
name|String
name|outputFileName
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"o"
argument_list|)
decl_stmt|;
name|String
name|processor
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
if|if
condition|(
name|processor
operator|==
literal|null
condition|)
block|{
name|processor
operator|=
name|defaultProcessor
expr_stmt|;
block|}
name|Flags
name|flags
init|=
operator|new
name|Flags
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
name|flags
operator|.
name|setRecoveryMode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"f"
argument_list|)
condition|)
block|{
name|flags
operator|.
name|setFixTxIds
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"v"
argument_list|)
condition|)
block|{
name|flags
operator|.
name|setPrintToScreen
argument_list|()
expr_stmt|;
block|}
return|return
name|go
argument_list|(
name|inputFileName
argument_list|,
name|outputFileName
argument_list|,
name|processor
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * main() runs the offline edits viewer using ToolRunner    *    * @param argv Command line parameters.    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|OfflineEditsViewer
argument_list|()
argument_list|,
name|argv
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
DECL|method|isHelpOption (String arg)
specifier|private
specifier|static
name|boolean
name|isHelpOption
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
return|return
name|arg
operator|.
name|equalsIgnoreCase
argument_list|(
name|HELP_OPT
argument_list|)
operator|||
name|arg
operator|.
name|equalsIgnoreCase
argument_list|(
name|HELP_LONGOPT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

