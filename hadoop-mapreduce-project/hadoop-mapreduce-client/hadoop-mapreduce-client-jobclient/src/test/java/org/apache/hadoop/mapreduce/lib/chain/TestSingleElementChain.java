begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.chain
package|package
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
name|chain
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
name|HadoopTestCase
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
name|Job
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
name|TokenCounterMapper
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
name|reduce
operator|.
name|IntSumReducer
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
name|assertEquals
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Runs wordcount by adding single mapper and single reducer to chain  */
end_comment

begin_class
DECL|class|TestSingleElementChain
specifier|public
class|class
name|TestSingleElementChain
extends|extends
name|HadoopTestCase
block|{
DECL|field|localPathRoot
specifier|private
specifier|static
name|String
name|localPathRoot
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
decl_stmt|;
DECL|method|TestSingleElementChain ()
specifier|public
name|TestSingleElementChain
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|HadoopTestCase
operator|.
name|LOCAL_MR
argument_list|,
name|HadoopTestCase
operator|.
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// test chain mapper and reducer by adding single mapper and reducer to chain
annotation|@
name|Test
DECL|method|testNoChain ()
specifier|public
name|void
name|testNoChain
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
literal|"testing/chain/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|localPathRoot
argument_list|,
literal|"testing/chain/output"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"a\nb\na\n"
decl_stmt|;
name|String
name|expectedOutput
init|=
literal|"a\t2\nb\t1\n"
decl_stmt|;
name|Configuration
name|conf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"chain"
argument_list|)
expr_stmt|;
name|ChainMapper
operator|.
name|addMapper
argument_list|(
name|job
argument_list|,
name|TokenCounterMapper
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ChainReducer
operator|.
name|setReducer
argument_list|(
name|job
argument_list|,
name|IntSumReducer
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|IntWritable
operator|.
name|class
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Job failed"
argument_list|,
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Outputs doesn't match"
argument_list|,
name|expectedOutput
argument_list|,
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|outDir
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

