begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.dancing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|dancing
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|StringTokenizer
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
name|mapreduce
operator|.
name|*
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
name|input
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Launch a distributed pentomino solver.  * It generates a complete list of prefixes of length N with each unique prefix  * as a separate line. A prefix is a sequence of N integers that denote the   * index of the row that is choosen for each column in order. Note that the  * next column is heuristically choosen by the solver, so it is dependant on  * the previous choice. That file is given as the input to  * map/reduce. The output key/value are the move prefix/solution as Text/Text.  */
end_comment

begin_class
DECL|class|DistributedPentomino
specifier|public
class|class
name|DistributedPentomino
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|PENT_DEPTH
specifier|private
specifier|static
specifier|final
name|int
name|PENT_DEPTH
init|=
literal|5
decl_stmt|;
DECL|field|PENT_WIDTH
specifier|private
specifier|static
specifier|final
name|int
name|PENT_WIDTH
init|=
literal|9
decl_stmt|;
DECL|field|PENT_HEIGHT
specifier|private
specifier|static
specifier|final
name|int
name|PENT_HEIGHT
init|=
literal|10
decl_stmt|;
DECL|field|DEFAULT_MAPS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAPS
init|=
literal|2000
decl_stmt|;
comment|/**    * Each map takes a line, which represents a prefix move and finds all of     * the solutions that start with that prefix. The output is the prefix as    * the key and the solution as the value.    */
DECL|class|PentMap
specifier|public
specifier|static
class|class
name|PentMap
extends|extends
name|Mapper
argument_list|<
name|WritableComparable
argument_list|<
name|?
argument_list|>
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|width
specifier|private
name|int
name|width
decl_stmt|;
DECL|field|height
specifier|private
name|int
name|height
decl_stmt|;
DECL|field|depth
specifier|private
name|int
name|depth
decl_stmt|;
DECL|field|pent
specifier|private
name|Pentomino
name|pent
decl_stmt|;
DECL|field|prefixString
specifier|private
name|Text
name|prefixString
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
comment|/**      * For each solution, generate the prefix and a string representation      * of the solution. The solution starts with a newline, so that the output      * looks like:      *<prefix>,      *<solution>      *       */
DECL|class|SolutionCatcher
class|class
name|SolutionCatcher
implements|implements
name|DancingLinks
operator|.
name|SolutionAcceptor
argument_list|<
name|Pentomino
operator|.
name|ColumnName
argument_list|>
block|{
DECL|method|solution (List<List<Pentomino.ColumnName>> answer)
specifier|public
name|void
name|solution
parameter_list|(
name|List
argument_list|<
name|List
argument_list|<
name|Pentomino
operator|.
name|ColumnName
argument_list|>
argument_list|>
name|answer
parameter_list|)
block|{
name|String
name|board
init|=
name|Pentomino
operator|.
name|stringifySolution
argument_list|(
name|width
argument_list|,
name|height
argument_list|,
name|answer
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|write
argument_list|(
name|prefixString
argument_list|,
operator|new
name|Text
argument_list|(
literal|"\n"
operator|+
name|board
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|pent
operator|.
name|getCategory
argument_list|(
name|answer
argument_list|)
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
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
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
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
name|ie
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Break the prefix string into moves (a sequence of integer row ids that       * will be selected for each column in order). Find all solutions with      * that prefix.      */
DECL|method|map (WritableComparable<?> key, Text value,Context context)
specifier|public
name|void
name|map
parameter_list|(
name|WritableComparable
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|prefixString
operator|=
name|value
expr_stmt|;
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|prefixString
operator|.
name|toString
argument_list|()
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|int
index|[]
name|prefix
init|=
operator|new
name|int
index|[
name|depth
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|num
init|=
name|itr
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|prefix
index|[
name|idx
operator|++
index|]
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
name|pent
operator|.
name|solve
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|public
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|depth
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|DEPTH
argument_list|,
name|PENT_DEPTH
argument_list|)
expr_stmt|;
name|width
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|WIDTH
argument_list|,
name|PENT_WIDTH
argument_list|)
expr_stmt|;
name|height
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|HEIGHT
argument_list|,
name|PENT_HEIGHT
argument_list|)
expr_stmt|;
name|pent
operator|=
operator|(
name|Pentomino
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|Pentomino
operator|.
name|CLASS
argument_list|,
name|OneSidedPentomino
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|pent
operator|.
name|initialize
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
name|pent
operator|.
name|setPrinter
argument_list|(
operator|new
name|SolutionCatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create the input file with all of the possible combinations of the     * given depth.    * @param fs the filesystem to write into    * @param dir the directory to write the input file into    * @param pent the puzzle     * @param depth the depth to explore when generating prefixes    */
DECL|method|createInputDirectory (FileSystem fs, Path dir, Pentomino pent, int depth )
specifier|private
specifier|static
name|long
name|createInputDirectory
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|dir
parameter_list|,
name|Pentomino
name|pent
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|splits
init|=
name|pent
operator|.
name|getSplits
argument_list|(
name|depth
argument_list|)
decl_stmt|;
name|Path
name|input
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"part1"
argument_list|)
decl_stmt|;
name|PrintStream
name|file
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|input
argument_list|)
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
index|[]
name|prefix
range|:
name|splits
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|file
operator|.
name|print
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|file
operator|.
name|print
argument_list|(
name|prefix
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|file
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fs
operator|.
name|getFileStatus
argument_list|(
name|input
argument_list|)
operator|.
name|getLen
argument_list|()
return|;
block|}
comment|/**    * Launch the solver on 9x10 board and the one sided pentominos.    * This takes about 2.5 hours on 20 nodes with 2 cpus/node.    * Splits the job into 2000 maps and 1 reduce.    */
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
name|DistributedPentomino
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
name|out
operator|.
name|println
argument_list|(
literal|"pentomino<output>"
argument_list|)
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
return|return
literal|2
return|;
block|}
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|int
name|width
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|WIDTH
argument_list|,
name|PENT_WIDTH
argument_list|)
decl_stmt|;
name|int
name|height
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|HEIGHT
argument_list|,
name|PENT_HEIGHT
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|Pentomino
operator|.
name|DEPTH
argument_list|,
name|PENT_DEPTH
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Pentomino
argument_list|>
name|pentClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|Pentomino
operator|.
name|CLASS
argument_list|,
name|OneSidedPentomino
operator|.
name|class
argument_list|,
name|Pentomino
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|numMaps
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
name|DEFAULT_MAPS
argument_list|)
decl_stmt|;
name|Path
name|output
init|=
operator|new
name|Path
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Path
name|input
init|=
operator|new
name|Path
argument_list|(
name|output
operator|+
literal|"_input"
argument_list|)
decl_stmt|;
name|FileSystem
name|fileSys
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|Job
name|job
init|=
operator|new
name|Job
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|PentMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"dancingElephant"
argument_list|)
expr_stmt|;
name|Pentomino
name|pent
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|pentClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|pent
operator|.
name|initialize
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
name|long
name|inputSize
init|=
name|createInputDirectory
argument_list|(
name|fileSys
argument_list|,
name|input
argument_list|,
name|pent
argument_list|,
name|depth
argument_list|)
decl_stmt|;
comment|// for forcing the number of maps
name|FileInputFormat
operator|.
name|setMaxInputSplitSize
argument_list|(
name|job
argument_list|,
operator|(
name|inputSize
operator|/
name|numMaps
operator|)
argument_list|)
expr_stmt|;
comment|// the keys are the prefix strings
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the values are puzzle solutions
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
name|setMapperClass
argument_list|(
name|PentMap
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|Reducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
operator|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
finally|finally
block|{
name|fileSys
operator|.
name|delete
argument_list|(
name|input
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

