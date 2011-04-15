begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.loadGenerator
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|loadGenerator
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
name|util
operator|.
name|EnumSet
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
name|fs
operator|.
name|CreateFlag
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
name|FSDataOutputStream
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
name|FileContext
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
name|Options
operator|.
name|CreateOpts
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
comment|/**  * This program reads the directory structure and file structure from  * the input directory and creates the namespace in the file system  * specified by the configuration in the specified root.  * All the files are filled with 'a'.  *   * The synopsis of the command is  * java DataGenerator   *   -inDir<inDir>: input directory name where directory/file structures  *                   are stored. Its default value is the current directory.  *   -root<root>: the name of the root directory which the new namespace   *                 is going to be placed under.   *                 Its default value is "/testLoadSpace".  */
end_comment

begin_class
DECL|class|DataGenerator
specifier|public
class|class
name|DataGenerator
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|inDir
specifier|private
name|File
name|inDir
init|=
name|StructureGenerator
operator|.
name|DEFAULT_STRUCTURE_DIRECTORY
decl_stmt|;
DECL|field|root
specifier|private
name|Path
name|root
init|=
name|DEFAULT_ROOT
decl_stmt|;
DECL|field|fc
specifier|private
name|FileContext
name|fc
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|final
specifier|static
specifier|private
name|long
name|BLOCK_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|USAGE
specifier|final
specifier|static
specifier|private
name|String
name|USAGE
init|=
literal|"java DataGenerator "
operator|+
literal|"-inDir<inDir> "
operator|+
literal|"-root<root>"
decl_stmt|;
comment|/** default name of the root where the test namespace will be placed under */
DECL|field|DEFAULT_ROOT
specifier|final
specifier|static
name|Path
name|DEFAULT_ROOT
init|=
operator|new
name|Path
argument_list|(
literal|"/testLoadSpace"
argument_list|)
decl_stmt|;
comment|/** Main function.    * It first parses the command line arguments.    * It then reads the directory structure from the input directory     * structure file and creates directory structure in the file system    * namespace. Afterwards it reads the file attributes and creates files     * in the file. All file content is filled with 'a'.    */
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
name|exitCode
operator|=
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
return|return
name|exitCode
return|;
block|}
name|genDirStructure
argument_list|()
expr_stmt|;
name|genFiles
argument_list|()
expr_stmt|;
return|return
name|exitCode
return|;
block|}
comment|/** Parse the command line arguments and initialize the data */
DECL|method|init (String[] args)
specifier|private
name|int
name|init
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
comment|// initialize file system handle
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Can not initialize the file system: "
operator|+
name|ioe
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
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
literal|"-root"
argument_list|)
condition|)
block|{
name|root
operator|=
operator|new
name|Path
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
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
literal|"-inDir"
argument_list|)
condition|)
block|{
name|inDir
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|USAGE
argument_list|)
expr_stmt|;
name|ToolRunner
operator|.
name|printGenericCommandUsage
argument_list|(
name|System
operator|.
name|err
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
block|}
return|return
literal|0
return|;
block|}
comment|/** Read directory structure file under the input directory.    * Create each directory under the specified root.    * The directory names are relative to the specified root.    */
DECL|method|genDirStructure ()
specifier|private
name|void
name|genDirStructure
parameter_list|()
throws|throws
name|IOException
block|{
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
operator|new
name|File
argument_list|(
name|inDir
argument_list|,
name|StructureGenerator
operator|.
name|DIR_STRUCTURE_FILE_NAME
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|fc
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
name|root
operator|+
name|line
argument_list|)
argument_list|,
name|FileContext
operator|.
name|DEFAULT_PERM
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Read file structure file under the input directory.    * Create each file under the specified root.    * The file names are relative to the root.    */
DECL|method|genFiles ()
specifier|private
name|void
name|genFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
operator|new
name|File
argument_list|(
name|inDir
argument_list|,
name|StructureGenerator
operator|.
name|FILE_STRUCTURE_FILE_NAME
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|tokens
init|=
name|line
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expect at most 2 tokens per line: "
operator|+
name|line
argument_list|)
throw|;
block|}
name|String
name|fileName
init|=
name|root
operator|+
name|tokens
index|[
literal|0
index|]
decl_stmt|;
name|long
name|fileSize
init|=
call|(
name|long
call|)
argument_list|(
name|BLOCK_SIZE
operator|*
name|Double
operator|.
name|parseDouble
argument_list|(
name|tokens
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|genFile
argument_list|(
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Create a file with the name<code>file</code> and     * a length of<code>fileSize</code>. The file is filled with character 'a'.    */
DECL|method|genFile (Path file, long fileSize)
specifier|private
name|void
name|genFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fc
operator|.
name|create
argument_list|(
name|file
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
argument_list|,
name|CreateOpts
operator|.
name|createParent
argument_list|()
argument_list|,
name|CreateOpts
operator|.
name|bufferSize
argument_list|(
literal|4096
argument_list|)
argument_list|,
name|CreateOpts
operator|.
name|repFac
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Main program.    *     * @param args Command line arguments    * @throws Exception    */
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
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
operator|new
name|DataGenerator
argument_list|()
argument_list|,
name|args
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
block|}
end_class

end_unit

