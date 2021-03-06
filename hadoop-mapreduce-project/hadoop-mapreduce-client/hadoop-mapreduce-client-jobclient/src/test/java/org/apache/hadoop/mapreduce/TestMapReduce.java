begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|BufferedWriter
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
name|OutputStreamWriter
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
name|util
operator|.
name|Iterator
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
name|FileUtil
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
name|IntWritable
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
name|SequenceFile
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
name|input
operator|.
name|SequenceFileInputFormat
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|MapFileOutputFormat
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
name|SequenceFileOutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**********************************************************  * MapredLoadTest generates a bunch of work that exercises  * a Hadoop Map-Reduce system (and DFS, too).  It goes through  * the following steps:  *  * 1) Take inputs 'range' and 'counts'.  * 2) Generate 'counts' random integers between 0 and range-1.  * 3) Create a file that lists each integer between 0 and range-1,  *    and lists the number of times that integer was generated.  * 4) Emit a (very large) file that contains all the integers  *    in the order generated.  * 5) After the file has been generated, read it back and count  *    how many times each int was generated.  * 6) Compare this big count-map against the original one.  If  *    they match, then SUCCESS!  Otherwise, FAILURE!  *  * OK, that's how we can think about it.  What are the map-reduce  * steps that get the job done?  *  * 1) In a non-mapred thread, take the inputs 'range' and 'counts'.  * 2) In a non-mapread thread, generate the answer-key and write to disk.  * 3) In a mapred job, divide the answer key into K jobs.  * 4) A mapred 'generator' task consists of K map jobs.  Each reads  *    an individual "sub-key", and generates integers according to  *    to it (though with a random ordering).  * 5) The generator's reduce task agglomerates all of those files  *    into a single one.  * 6) A mapred 'reader' task consists of M map jobs.  The output  *    file is cut into M pieces. Each of the M jobs counts the   *    individual ints in its chunk and creates a map of all seen ints.  * 7) A mapred job integrates all the count files into a single one.  *  **********************************************************/
end_comment

begin_class
DECL|class|TestMapReduce
specifier|public
class|class
name|TestMapReduce
block|{
DECL|field|TEST_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|,
literal|"TestMapReduce-mapreduce"
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
static|static
block|{
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
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
name|fs
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Modified to make it a junit test.    * The RandomGen Job does the actual work of creating    * a huge file of assorted numbers.  It receives instructions    * as to how many times each number should be counted.  Then    * it emits those numbers in a crazy order.    *    * The map() function takes a key/val pair that describes    * a value-to-be-emitted (the key) and how many times it     * should be emitted (the value), aka "numtimes".  map() then    * emits a series of intermediate key/val pairs.  It emits    * 'numtimes' of these.  The key is a random number and the    * value is the 'value-to-be-emitted'.    *    * The system collates and merges these pairs according to    * the random number.  reduce() function takes in a key/value    * pair that consists of a crazy random number and a series    * of values that should be emitted.  The random number key    * is now dropped, and reduce() emits a pair for every intermediate value.    * The emitted key is an intermediate value.  The emitted value    * is just a blank string.  Thus, we've created a huge file    * of numbers in random order, but where each number appears    * as many times as we were instructed.    */
DECL|class|RandomGenMapper
specifier|static
class|class
name|RandomGenMapper
extends|extends
name|Mapper
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (IntWritable key, IntWritable val, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|val
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|int
name|randomVal
init|=
name|key
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|randomCount
init|=
name|val
operator|.
name|get
argument_list|()
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
name|randomCount
condition|;
name|i
operator|++
control|)
block|{
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|randomVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    */
DECL|class|RandomGenReducer
specifier|static
class|class
name|RandomGenReducer
extends|extends
name|Reducer
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (IntWritable key, Iterable<IntWritable> it, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|IntWritable
argument_list|>
name|it
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|IntWritable
name|iw
range|:
name|it
control|)
block|{
name|context
operator|.
name|write
argument_list|(
name|iw
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The RandomCheck Job does a lot of our work.  It takes    * in a num/string keyspace, and transforms it into a    * key/count(int) keyspace.    *    * The map() function just emits a num/1 pair for every    * num/string input pair.    *    * The reduce() function sums up all the 1s that were    * emitted for a single key.  It then emits the key/total    * pair.    *    * This is used to regenerate the random number "answer key".    * Each key here is a random number, and the count is the    * number of times the number was emitted.    */
DECL|class|RandomCheckMapper
specifier|static
class|class
name|RandomCheckMapper
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
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (WritableComparable<?> key, Text val, Context context)
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
name|val
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    */
DECL|class|RandomCheckReducer
specifier|static
class|class
name|RandomCheckReducer
extends|extends
name|Reducer
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (IntWritable key, Iterable<IntWritable> it, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|Iterable
argument_list|<
name|IntWritable
argument_list|>
name|it
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|int
name|keyint
init|=
name|key
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IntWritable
name|iw
range|:
name|it
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|keyint
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|count
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The Merge Job is a really simple one.  It takes in    * an int/int key-value set, and emits the same set.    * But it merges identical keys by adding their values.    *    * Thus, the map() function is just the identity function    * and reduce() just sums.  Nothing to see here!    */
DECL|class|MergeMapper
specifier|static
class|class
name|MergeMapper
extends|extends
name|Mapper
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|map (IntWritable key, IntWritable val, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|IntWritable
name|val
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|int
name|keyint
init|=
name|key
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|valint
init|=
name|val
operator|.
name|get
argument_list|()
decl_stmt|;
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|keyint
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|valint
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MergeReducer
specifier|static
class|class
name|MergeReducer
extends|extends
name|Reducer
argument_list|<
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (IntWritable key, Iterator<IntWritable> it, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|IntWritable
name|key
parameter_list|,
name|Iterator
argument_list|<
name|IntWritable
argument_list|>
name|it
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|int
name|keyint
init|=
name|key
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|total
operator|+=
name|it
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|write
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|keyint
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|range
specifier|private
specifier|static
name|int
name|range
init|=
literal|10
decl_stmt|;
DECL|field|counts
specifier|private
specifier|static
name|int
name|counts
init|=
literal|100
decl_stmt|;
DECL|field|r
specifier|private
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapred ()
specifier|public
name|void
name|testMapred
parameter_list|()
throws|throws
name|Exception
block|{
name|launch
argument_list|()
expr_stmt|;
block|}
DECL|method|launch ()
specifier|private
specifier|static
name|void
name|launch
parameter_list|()
throws|throws
name|Exception
block|{
comment|//
comment|// Generate distribution of ints.  This is the answer key.
comment|//
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|int
name|countsToGo
init|=
name|counts
decl_stmt|;
name|int
name|dist
index|[]
init|=
operator|new
name|int
index|[
name|range
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
name|range
condition|;
name|i
operator|++
control|)
block|{
name|double
name|avgInts
init|=
operator|(
literal|1.0
operator|*
name|countsToGo
operator|)
operator|/
operator|(
name|range
operator|-
name|i
operator|)
decl_stmt|;
name|dist
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|round
argument_list|(
name|avgInts
operator|+
operator|(
name|Math
operator|.
name|sqrt
argument_list|(
name|avgInts
argument_list|)
operator|*
name|r
operator|.
name|nextGaussian
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|countsToGo
operator|-=
name|dist
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|countsToGo
operator|>
literal|0
condition|)
block|{
name|dist
index|[
name|dist
operator|.
name|length
operator|-
literal|1
index|]
operator|+=
name|countsToGo
expr_stmt|;
block|}
comment|//
comment|// Write the answer key to a file.
comment|//
name|Path
name|testdir
init|=
operator|new
name|Path
argument_list|(
name|TEST_DIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|testdir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|testdir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|randomIns
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"genins"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|randomIns
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|randomIns
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|answerkey
init|=
operator|new
name|Path
argument_list|(
name|randomIns
argument_list|,
literal|"answer.key"
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Writer
name|out
init|=
name|SequenceFile
operator|.
name|createWriter
argument_list|(
name|fs
argument_list|,
name|conf
argument_list|,
name|answerkey
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|SequenceFile
operator|.
name|CompressionType
operator|.
name|NONE
argument_list|)
decl_stmt|;
try|try
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
name|range
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|append
argument_list|(
operator|new
name|IntWritable
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|dist
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|printFiles
argument_list|(
name|randomIns
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//
comment|// Now we need to generate the random numbers according to
comment|// the above distribution.
comment|//
comment|// We create a lot of map tasks, each of which takes at least
comment|// one "line" of the distribution.  (That is, a certain number
comment|// X is to be generated Y number of times.)
comment|//
comment|// A map task emits Y key/val pairs.  The val is X.  The key
comment|// is a randomly-generated number.
comment|//
comment|// The reduce task gets its input sorted by key.  That is, sorted
comment|// in random order.  It then emits a single line of text that
comment|// for the given values.  It does not emit the key.
comment|//
comment|// Because there's just one reduce task, we emit a single big
comment|// file of random numbers.
comment|//
name|Path
name|randomOuts
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"genouts"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|randomOuts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Job
name|genJob
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|genJob
argument_list|,
name|randomIns
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setInputFormatClass
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setMapperClass
argument_list|(
name|RandomGenMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|genJob
argument_list|,
name|randomOuts
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setReducerClass
argument_list|(
name|RandomGenReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|genJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|printFiles
argument_list|(
name|randomOuts
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//
comment|// Next, we read the big file in and regenerate the
comment|// original map.  It's split into a number of parts.
comment|// (That number is 'intermediateReduces'.)
comment|//
comment|// We have many map tasks, each of which read at least one
comment|// of the output numbers.  For each number read in, the
comment|// map task emits a key/value pair where the key is the
comment|// number and the value is "1".
comment|//
comment|// We have a single reduce task, which receives its input
comment|// sorted by the key emitted above.  For each key, there will
comment|// be a certain number of "1" values.  The reduce task sums
comment|// these values to compute how many times the given key was
comment|// emitted.
comment|//
comment|// The reduce task then emits a key/val pair where the key
comment|// is the number in question, and the value is the number of
comment|// times the key was emitted.  This is the same format as the
comment|// original answer key (except that numbers emitted zero times
comment|// will not appear in the regenerated key.)  The answer set
comment|// is split into a number of pieces.  A final MapReduce job
comment|// will merge them.
comment|//
comment|// There's not really a need to go to 10 reduces here
comment|// instead of 1.  But we want to test what happens when
comment|// you have multiple reduces at once.
comment|//
name|int
name|intermediateReduces
init|=
literal|10
decl_stmt|;
name|Path
name|intermediateOuts
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"intermediateouts"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|intermediateOuts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Job
name|checkJob
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|checkJob
argument_list|,
name|randomOuts
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setMapperClass
argument_list|(
name|RandomCheckMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|checkJob
argument_list|,
name|intermediateOuts
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setOutputFormatClass
argument_list|(
name|MapFileOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setReducerClass
argument_list|(
name|RandomCheckReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|setNumReduceTasks
argument_list|(
name|intermediateReduces
argument_list|)
expr_stmt|;
name|checkJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|printFiles
argument_list|(
name|intermediateOuts
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//
comment|// OK, now we take the output from the last job and
comment|// merge it down to a single file.  The map() and reduce()
comment|// functions don't really do anything except reemit tuples.
comment|// But by having a single reduce task here, we end up merging
comment|// all the files.
comment|//
name|Path
name|finalOuts
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"finalouts"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|finalOuts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Job
name|mergeJob
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|mergeJob
argument_list|,
name|intermediateOuts
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setInputFormatClass
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setMapperClass
argument_list|(
name|MergeMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|mergeJob
argument_list|,
name|finalOuts
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setOutputKeyClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setOutputFormatClass
argument_list|(
name|SequenceFileOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setReducerClass
argument_list|(
name|MergeReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mergeJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|printFiles
argument_list|(
name|finalOuts
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//
comment|// Finally, we compare the reconstructed answer key with the
comment|// original one.  Remember, we need to ignore zero-count items
comment|// in the original key.
comment|//
name|boolean
name|success
init|=
literal|true
decl_stmt|;
name|Path
name|recomputedkey
init|=
operator|new
name|Path
argument_list|(
name|finalOuts
argument_list|,
literal|"part-r-00000"
argument_list|)
decl_stmt|;
name|SequenceFile
operator|.
name|Reader
name|in
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
name|recomputedkey
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|int
name|totalseen
init|=
literal|0
decl_stmt|;
try|try
block|{
name|IntWritable
name|key
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
name|IntWritable
name|val
init|=
operator|new
name|IntWritable
argument_list|()
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
name|range
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dist
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|in
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Cannot read entry "
operator|+
name|i
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|key
operator|.
name|get
argument_list|()
operator|==
name|i
operator|)
operator|&&
operator|(
name|val
operator|.
name|get
argument_list|()
operator|==
name|dist
index|[
name|i
index|]
operator|)
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Mismatch!  Pos="
operator|+
name|key
operator|.
name|get
argument_list|()
operator|+
literal|", i="
operator|+
name|i
operator|+
literal|", val="
operator|+
name|val
operator|.
name|get
argument_list|()
operator|+
literal|", dist[i]="
operator|+
name|dist
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
block|}
name|totalseen
operator|+=
name|val
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unnecessary lines in recomputed key!"
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|int
name|originalTotal
init|=
literal|0
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
name|dist
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|originalTotal
operator|+=
name|dist
index|[
name|i
index|]
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Original sum: "
operator|+
name|originalTotal
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Recomputed sum: "
operator|+
name|totalseen
argument_list|)
expr_stmt|;
comment|//
comment|// Write to "results" whether the test succeeded or not.
comment|//
name|Path
name|resultFile
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"results"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|bw
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|resultFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|bw
operator|.
name|write
argument_list|(
literal|"Success="
operator|+
name|success
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Success="
operator|+
name|success
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"testMapRed failed"
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|testdir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|printTextFile (FileSystem fs, Path p)
specifier|private
specifier|static
name|void
name|printTextFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
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
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|p
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Row: "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|printSequenceFile (FileSystem fs, Path p, Configuration conf)
specifier|private
specifier|static
name|void
name|printSequenceFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|SequenceFile
operator|.
name|Reader
name|r
init|=
operator|new
name|SequenceFile
operator|.
name|Reader
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Object
name|key
init|=
literal|null
decl_stmt|;
name|Object
name|value
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|key
operator|=
name|r
operator|.
name|next
argument_list|(
name|key
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|r
operator|.
name|getCurrentValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  Row: "
operator|+
name|key
operator|+
literal|", "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isSequenceFile (FileSystem fs, Path f)
specifier|private
specifier|static
name|boolean
name|isSequenceFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|seq
init|=
literal|"SEQ"
operator|.
name|getBytes
argument_list|()
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
name|seq
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|seq
index|[
name|i
index|]
operator|!=
name|in
operator|.
name|read
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|printFiles (Path dir, Configuration conf)
specifier|private
specifier|static
name|void
name|printFiles
parameter_list|(
name|Path
name|dir
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|dir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|f
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|dir
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reading "
operator|+
name|f
operator|.
name|getPath
argument_list|()
operator|+
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  it is a map file."
argument_list|)
expr_stmt|;
name|printSequenceFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"data"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSequenceFile
argument_list|(
name|fs
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  it is a sequence file."
argument_list|)
expr_stmt|;
name|printSequenceFile
argument_list|(
name|fs
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  it is a text file."
argument_list|)
expr_stmt|;
name|printTextFile
argument_list|(
name|fs
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Launches all the tasks in order.    */
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
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: TestMapReduce<range><counts>"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Note: a good test will have a<counts> value"
operator|+
literal|" that is substantially larger than the<range>"
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|range
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|argv
index|[
name|i
operator|++
index|]
argument_list|)
expr_stmt|;
name|counts
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|argv
index|[
name|i
operator|++
index|]
argument_list|)
expr_stmt|;
try|try
block|{
name|launch
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

