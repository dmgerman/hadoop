begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.lib.aggregate
package|package
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
name|aggregate
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
name|fs
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
name|io
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
name|mapred
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
name|mapred
operator|.
name|lib
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
name|MapReduceTestUtil
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_class
DECL|class|TestAggregates
specifier|public
class|class
name|TestAggregates
extends|extends
name|TestCase
block|{
DECL|field|idFormat
specifier|private
specifier|static
name|NumberFormat
name|idFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|idFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|idFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testAggregates ()
specifier|public
name|void
name|testAggregates
parameter_list|()
throws|throws
name|Exception
block|{
name|launch
argument_list|()
expr_stmt|;
block|}
DECL|method|launch ()
specifier|public
specifier|static
name|void
name|launch
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|(
name|TestAggregates
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|numOfInputLines
init|=
literal|20
decl_stmt|;
name|Path
name|OUTPUT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"build/test/output_for_aggregates_test"
argument_list|)
decl_stmt|;
name|Path
name|INPUT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"build/test/input_for_aggregates_test"
argument_list|)
decl_stmt|;
name|String
name|inputFile
init|=
literal|"input.txt"
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|INPUT_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|INPUT_DIR
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|OUTPUT_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|StringBuffer
name|inputData
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|StringBuffer
name|expectedOutput
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"max\t19\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"min\t1\n"
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|fileOut
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|INPUT_DIR
argument_list|,
name|inputFile
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numOfInputLines
condition|;
name|i
operator|++
control|)
block|{
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"count_"
argument_list|)
operator|.
name|append
argument_list|(
name|idFormat
operator|.
name|format
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|inputData
operator|.
name|append
argument_list|(
name|idFormat
operator|.
name|format
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
name|inputData
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|idFormat
operator|.
name|format
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|inputData
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"value_as_string_max\t9\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"value_as_string_min\t1\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
literal|"uniq_count\t15\n"
argument_list|)
expr_stmt|;
name|fileOut
operator|.
name|write
argument_list|(
name|inputData
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
expr_stmt|;
name|fileOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"inputData:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|inputData
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|,
name|TestAggregates
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|INPUT_DIR
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
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
name|OUTPUT_DIR
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
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|Text
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
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|ValueAggregatorMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|ValueAggregatorReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerClass
argument_list|(
name|ValueAggregatorCombiner
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInt
argument_list|(
literal|"aggregator.descriptor.num"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
literal|"aggregator.descriptor.0"
argument_list|,
literal|"UserDefined,org.apache.hadoop.mapred.lib.aggregate.AggregatorTests"
argument_list|)
expr_stmt|;
name|job
operator|.
name|setLong
argument_list|(
literal|"aggregate.max.num.unique.values"
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
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
name|outPath
init|=
operator|new
name|Path
argument_list|(
name|OUTPUT_DIR
argument_list|,
literal|"part-00000"
argument_list|)
decl_stmt|;
name|String
name|outdata
init|=
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|outPath
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"full out data:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|outdata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|outdata
operator|=
name|outdata
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|expectedOutput
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedOutput
operator|.
name|toString
argument_list|()
argument_list|,
name|outdata
argument_list|)
expr_stmt|;
comment|//fs.delete(OUTPUT_DIR);
name|fs
operator|.
name|delete
argument_list|(
name|INPUT_DIR
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|launch
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

