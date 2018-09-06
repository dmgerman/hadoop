begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
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
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|EOFException
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
name|FileInputStream
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
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSEditLogLoader
operator|.
name|PositionTrackingInputStream
import|;
end_import

begin_comment
comment|/**  * OfflineImageViewer to dump the contents of an Hadoop image file to XML  * or the console.  Main entry point into utility, either via the  * command line or programmatically.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|OfflineImageViewer
specifier|public
class|class
name|OfflineImageViewer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OfflineImageViewer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|usage
specifier|private
specifier|final
specifier|static
name|String
name|usage
init|=
literal|"Usage: bin/hdfs oiv_legacy [OPTIONS] -i INPUTFILE -o OUTPUTFILE\n"
operator|+
literal|"Offline Image Viewer\n"
operator|+
literal|"View a Hadoop fsimage INPUTFILE using the specified PROCESSOR,\n"
operator|+
literal|"saving the results in OUTPUTFILE.\n"
operator|+
literal|"\n"
operator|+
literal|"The oiv utility will attempt to parse correctly formed image files\n"
operator|+
literal|"and will abort fail with mal-formed image files.\n"
operator|+
literal|"\n"
operator|+
literal|"The tool works offline and does not require a running cluster in\n"
operator|+
literal|"order to process an image file.\n"
operator|+
literal|"\n"
operator|+
literal|"The following image processors are available:\n"
operator|+
literal|"  * Ls: The default image processor generates an lsr-style listing\n"
operator|+
literal|"    of the files in the namespace, with the same fields in the same\n"
operator|+
literal|"    order.  Note that in order to correctly determine file sizes,\n"
operator|+
literal|"    this formatter cannot skip blocks and will override the\n"
operator|+
literal|"    -skipBlocks option.\n"
operator|+
literal|"  * Indented: This processor enumerates over all of the elements in\n"
operator|+
literal|"    the fsimage file, using levels of indentation to delineate\n"
operator|+
literal|"    sections within the file.\n"
operator|+
literal|"  * Delimited: Generate a text file with all of the elements common\n"
operator|+
literal|"    to both inodes and inodes-under-construction, separated by a\n"
operator|+
literal|"    delimiter. The default delimiter is \u0001, though this may be\n"
operator|+
literal|"    changed via the -delimiter argument. This processor also overrides\n"
operator|+
literal|"    the -skipBlocks option for the same reason as the Ls processor\n"
operator|+
literal|"  * XML: This processor creates an XML document with all elements of\n"
operator|+
literal|"    the fsimage enumerated, suitable for further analysis by XML\n"
operator|+
literal|"    tools.\n"
operator|+
literal|"  * FileDistribution: This processor analyzes the file size\n"
operator|+
literal|"    distribution in the image.\n"
operator|+
literal|"    -maxSize specifies the range [0, maxSize] of file sizes to be\n"
operator|+
literal|"     analyzed (128GB by default).\n"
operator|+
literal|"    -step defines the granularity of the distribution. (2MB by default)\n"
operator|+
literal|"    -format formats the output result in a human-readable fashion\n"
operator|+
literal|"     rather than a number of bytes. (false by default)\n"
operator|+
literal|"  * NameDistribution: This processor analyzes the file names\n"
operator|+
literal|"    in the image and prints total number of file names and how frequently\n"
operator|+
literal|"    file names are reused.\n"
operator|+
literal|"\n"
operator|+
literal|"Required command line arguments:\n"
operator|+
literal|"-i,--inputFile<arg>   FSImage file to process.\n"
operator|+
literal|"-o,--outputFile<arg>  Name of output file. If the specified\n"
operator|+
literal|"                       file exists, it will be overwritten.\n"
operator|+
literal|"\n"
operator|+
literal|"Optional command line arguments:\n"
operator|+
literal|"-p,--processor<arg>   Select which type of processor to apply\n"
operator|+
literal|"                       against image file."
operator|+
literal|" (Ls|XML|Delimited|Indented|FileDistribution|NameDistribution).\n"
operator|+
literal|"-h,--help              Display usage information and exit\n"
operator|+
literal|"-printToScreen         For processors that write to a file, also\n"
operator|+
literal|"                       output to screen. On large image files this\n"
operator|+
literal|"                       will dramatically increase processing time.\n"
operator|+
literal|"-skipBlocks            Skip inodes' blocks information. May\n"
operator|+
literal|"                       significantly decrease output.\n"
operator|+
literal|"                       (default = false).\n"
operator|+
literal|"-delimiter<arg>       Delimiting string to use with Delimited processor\n"
decl_stmt|;
DECL|field|skipBlocks
specifier|private
specifier|final
name|boolean
name|skipBlocks
decl_stmt|;
DECL|field|inputFile
specifier|private
specifier|final
name|String
name|inputFile
decl_stmt|;
DECL|field|processor
specifier|private
specifier|final
name|ImageVisitor
name|processor
decl_stmt|;
DECL|method|OfflineImageViewer (String inputFile, ImageVisitor processor, boolean skipBlocks)
specifier|public
name|OfflineImageViewer
parameter_list|(
name|String
name|inputFile
parameter_list|,
name|ImageVisitor
name|processor
parameter_list|,
name|boolean
name|skipBlocks
parameter_list|)
block|{
name|this
operator|.
name|inputFile
operator|=
name|inputFile
expr_stmt|;
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
name|this
operator|.
name|skipBlocks
operator|=
name|skipBlocks
expr_stmt|;
block|}
comment|/**    * Process image file.    */
DECL|method|go ()
specifier|public
name|void
name|go
parameter_list|()
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|PositionTrackingInputStream
name|tracker
init|=
literal|null
decl_stmt|;
name|ImageLoader
name|fsip
init|=
literal|null
decl_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
try|try
block|{
name|tracker
operator|=
operator|new
name|PositionTrackingInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|inputFile
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|tracker
argument_list|)
expr_stmt|;
name|int
name|imageVersionFile
init|=
name|findImageVersion
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|fsip
operator|=
name|ImageLoader
operator|.
name|LoaderFactory
operator|.
name|getLoader
argument_list|(
name|imageVersionFile
argument_list|)
expr_stmt|;
if|if
condition|(
name|fsip
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No image processor to read version "
operator|+
name|imageVersionFile
operator|+
literal|" is available."
argument_list|)
throw|;
name|fsip
operator|.
name|loadImage
argument_list|(
name|in
argument_list|,
name|processor
argument_list|,
name|skipBlocks
argument_list|)
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|done
condition|)
block|{
if|if
condition|(
name|tracker
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"image loading failed at offset "
operator|+
name|tracker
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to load image file."
argument_list|)
expr_stmt|;
block|}
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|in
argument_list|,
name|tracker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Check an fsimage datainputstream's version number.    *    * The datainput stream is returned at the same point as it was passed in;    * this method has no effect on the datainputstream's read pointer.    *    * @param in Datainputstream of fsimage    * @return Filesystem layout version of fsimage represented by stream    * @throws IOException If problem reading from in    */
DECL|method|findImageVersion (DataInputStream in)
specifier|private
name|int
name|findImageVersion
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|mark
argument_list|(
literal|42
argument_list|)
expr_stmt|;
comment|// arbitrary amount, resetting immediately
name|int
name|version
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|version
return|;
block|}
comment|/**    * Build command-line options and descriptions    */
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
literal|"outputFile"
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
literal|"inputFile"
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
literal|"h"
argument_list|,
literal|"help"
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
literal|"maxSize"
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
literal|"step"
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
literal|"format"
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
literal|"skipBlocks"
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
literal|"printToScreen"
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
literal|"delimiter"
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
comment|/**    * Entry point to command-line-driven operation.  User may specify    * options and start fsimage viewer from the command line.  Program    * will process image file and exit cleanly or, if an error is    * encountered, inform user and exit.    *    * @param args Command line options    * @throws IOException     */
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
name|IOException
block|{
name|Options
name|options
init|=
name|buildOptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return;
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
name|args
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
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
return|return;
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
comment|// print help and exit
name|printUsage
argument_list|()
expr_stmt|;
return|return;
block|}
name|boolean
name|skipBlocks
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"skipBlocks"
argument_list|)
decl_stmt|;
name|boolean
name|printToScreen
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"printToScreen"
argument_list|)
decl_stmt|;
name|String
name|inputFile
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"i"
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
argument_list|,
literal|"Ls"
argument_list|)
decl_stmt|;
name|String
name|outputFile
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"o"
argument_list|)
decl_stmt|;
name|String
name|delimiter
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"delimiter"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|delimiter
operator|==
literal|null
operator|||
name|processor
operator|.
name|equals
argument_list|(
literal|"Delimited"
argument_list|)
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Can only specify -delimiter with Delimited processor"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|()
expr_stmt|;
return|return;
block|}
name|ImageVisitor
name|v
decl_stmt|;
if|if
condition|(
name|processor
operator|.
name|equals
argument_list|(
literal|"Indented"
argument_list|)
condition|)
block|{
name|v
operator|=
operator|new
name|IndentedImageVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|processor
operator|.
name|equals
argument_list|(
literal|"XML"
argument_list|)
condition|)
block|{
name|v
operator|=
operator|new
name|XmlImageVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|processor
operator|.
name|equals
argument_list|(
literal|"Delimited"
argument_list|)
condition|)
block|{
name|v
operator|=
name|delimiter
operator|==
literal|null
condition|?
operator|new
name|DelimitedImageVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
else|:
operator|new
name|DelimitedImageVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|,
name|delimiter
argument_list|)
expr_stmt|;
name|skipBlocks
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|processor
operator|.
name|equals
argument_list|(
literal|"FileDistribution"
argument_list|)
condition|)
block|{
name|long
name|maxSize
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"maxSize"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|step
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|cmd
operator|.
name|getOptionValue
argument_list|(
literal|"step"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|formatOutput
init|=
name|cmd
operator|.
name|hasOption
argument_list|(
literal|"format"
argument_list|)
decl_stmt|;
name|v
operator|=
operator|new
name|FileDistributionVisitor
argument_list|(
name|outputFile
argument_list|,
name|maxSize
argument_list|,
name|step
argument_list|,
name|formatOutput
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|processor
operator|.
name|equals
argument_list|(
literal|"NameDistribution"
argument_list|)
condition|)
block|{
name|v
operator|=
operator|new
name|NameDistributionVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
operator|new
name|LsImageVisitor
argument_list|(
name|outputFile
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
name|skipBlocks
operator|=
literal|false
expr_stmt|;
block|}
try|try
block|{
name|OfflineImageViewer
name|d
init|=
operator|new
name|OfflineImageViewer
argument_list|(
name|inputFile
argument_list|,
name|v
argument_list|,
name|skipBlocks
argument_list|)
decl_stmt|;
name|d
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Input file ended unexpectedly.  Exiting"
argument_list|)
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
literal|"Encountered exception.  Exiting: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Print application usage instructions.    */
DECL|method|printUsage ()
specifier|private
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

