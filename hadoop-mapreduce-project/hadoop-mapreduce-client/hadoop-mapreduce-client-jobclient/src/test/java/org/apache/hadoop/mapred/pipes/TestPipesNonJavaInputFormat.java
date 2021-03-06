begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
package|;
end_package

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
name|IOException
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
name|FloatWritable
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
name|NullWritable
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
name|InputSplit
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
name|RecordReader
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
name|pipes
operator|.
name|TestPipeApplication
operator|.
name|FakeSplit
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
name|junit
operator|.
name|Assert
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestPipesNonJavaInputFormat
specifier|public
class|class
name|TestPipesNonJavaInputFormat
block|{
DECL|field|workSpace
specifier|private
specifier|static
name|File
name|workSpace
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestPipesNonJavaInputFormat
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-workSpace"
argument_list|)
decl_stmt|;
comment|/**    *  test PipesNonJavaInputFormat     */
annotation|@
name|Test
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|PipesNonJavaInputFormat
name|inputFormat
init|=
operator|new
name|PipesNonJavaInputFormat
argument_list|()
decl_stmt|;
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|Reporter
name|reporter
init|=
name|mock
argument_list|(
name|Reporter
operator|.
name|class
argument_list|)
decl_stmt|;
name|RecordReader
argument_list|<
name|FloatWritable
argument_list|,
name|NullWritable
argument_list|>
name|reader
init|=
name|inputFormat
operator|.
name|getRecordReader
argument_list|(
operator|new
name|FakeSplit
argument_list|()
argument_list|,
name|conf
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.0f
argument_list|,
name|reader
operator|.
name|getProgress
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
comment|// input and output files
name|File
name|input1
init|=
operator|new
name|File
argument_list|(
name|workSpace
operator|+
name|File
operator|.
name|separator
operator|+
literal|"input1"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|input1
operator|.
name|getParentFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|input1
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|input1
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|input1
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|input2
init|=
operator|new
name|File
argument_list|(
name|workSpace
operator|+
name|File
operator|.
name|separator
operator|+
literal|"input2"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|input2
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|input2
operator|.
name|createNewFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// set data for splits
name|conf
operator|.
name|set
argument_list|(
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
operator|.
name|INPUT_DIR
argument_list|,
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|input1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|+
literal|","
operator|+
name|StringUtils
operator|.
name|escapeString
argument_list|(
name|input2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|inputFormat
operator|.
name|getSplits
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|PipesNonJavaInputFormat
operator|.
name|PipesDummyRecordReader
name|dummyRecordReader
init|=
operator|new
name|PipesNonJavaInputFormat
operator|.
name|PipesDummyRecordReader
argument_list|(
name|conf
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// empty dummyRecordReader
name|assertNull
argument_list|(
name|dummyRecordReader
operator|.
name|createKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dummyRecordReader
operator|.
name|createValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dummyRecordReader
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|dummyRecordReader
operator|.
name|getProgress
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
comment|// test method next
name|assertTrue
argument_list|(
name|dummyRecordReader
operator|.
name|next
argument_list|(
operator|new
name|FloatWritable
argument_list|(
literal|2.0f
argument_list|)
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|dummyRecordReader
operator|.
name|getProgress
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|dummyRecordReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

