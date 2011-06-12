begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
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
name|io
operator|.
name|TestSequenceFile
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
name|BigMapOutput
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
name|GenericMRLoadGenerator
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
name|MRBench
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
name|ReliabilityTest
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
name|SortValidator
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
name|TestMapRed
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
name|TestSequenceFileInputFormat
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
name|TestTextInputFormat
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
name|ThreadedMapBenchmark
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
name|FailJob
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
name|SleepJob
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
name|ProgramDriver
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
name|NNBench
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
name|TestFileSystem
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
name|TestDFSIO
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
name|DFSCIOTest
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
name|DistributedFSCheck
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
name|FileBench
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
name|JHLogAnalyzer
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
name|SliveTest
import|;
end_import

begin_comment
comment|/**  * Driver for Map-reduce tests.  *  */
end_comment

begin_class
DECL|class|MapredTestDriver
specifier|public
class|class
name|MapredTestDriver
block|{
DECL|field|pgd
specifier|private
name|ProgramDriver
name|pgd
decl_stmt|;
DECL|method|MapredTestDriver ()
specifier|public
name|MapredTestDriver
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ProgramDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MapredTestDriver (ProgramDriver pgd)
specifier|public
name|MapredTestDriver
parameter_list|(
name|ProgramDriver
name|pgd
parameter_list|)
block|{
name|this
operator|.
name|pgd
operator|=
name|pgd
expr_stmt|;
try|try
block|{
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testsequencefile"
argument_list|,
name|TestSequenceFile
operator|.
name|class
argument_list|,
literal|"A test for flat files of binary key value pairs."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"threadedmapbench"
argument_list|,
name|ThreadedMapBenchmark
operator|.
name|class
argument_list|,
literal|"A map/reduce benchmark that compares the performance "
operator|+
literal|"of maps with multiple spills over maps with 1 spill"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"mrbench"
argument_list|,
name|MRBench
operator|.
name|class
argument_list|,
literal|"A map/reduce benchmark that can create many small jobs"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"mapredtest"
argument_list|,
name|TestMapRed
operator|.
name|class
argument_list|,
literal|"A map/reduce test check."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testsequencefileinputformat"
argument_list|,
name|TestSequenceFileInputFormat
operator|.
name|class
argument_list|,
literal|"A test for sequence file input format."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testtextinputformat"
argument_list|,
name|TestTextInputFormat
operator|.
name|class
argument_list|,
literal|"A test for text input format."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testmapredsort"
argument_list|,
name|SortValidator
operator|.
name|class
argument_list|,
literal|"A map/reduce program that validates the "
operator|+
literal|"map-reduce framework's sort."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testbigmapoutput"
argument_list|,
name|BigMapOutput
operator|.
name|class
argument_list|,
literal|"A map/reduce program that works on a very big "
operator|+
literal|"non-splittable file and does identity map/reduce"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"loadgen"
argument_list|,
name|GenericMRLoadGenerator
operator|.
name|class
argument_list|,
literal|"Generic map/reduce load generator"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"MRReliabilityTest"
argument_list|,
name|ReliabilityTest
operator|.
name|class
argument_list|,
literal|"A program that tests the reliability of the MR framework by "
operator|+
literal|"injecting faults/failures"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"fail"
argument_list|,
name|FailJob
operator|.
name|class
argument_list|,
literal|"a job that always fails"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"sleep"
argument_list|,
name|SleepJob
operator|.
name|class
argument_list|,
literal|"A job that sleeps at each map and reduce task."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"nnbench"
argument_list|,
name|NNBench
operator|.
name|class
argument_list|,
literal|"A benchmark that stresses the namenode."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testfilesystem"
argument_list|,
name|TestFileSystem
operator|.
name|class
argument_list|,
literal|"A test for FileSystem read/write."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
name|TestDFSIO
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|TestDFSIO
operator|.
name|class
argument_list|,
literal|"Distributed i/o benchmark."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"DFSCIOTest"
argument_list|,
name|DFSCIOTest
operator|.
name|class
argument_list|,
literal|""
operator|+
literal|"Distributed i/o benchmark of libhdfs."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"DistributedFSCheck"
argument_list|,
name|DistributedFSCheck
operator|.
name|class
argument_list|,
literal|"Distributed checkup of the file system consistency."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"filebench"
argument_list|,
name|FileBench
operator|.
name|class
argument_list|,
literal|"Benchmark SequenceFile(Input|Output)Format "
operator|+
literal|"(block,record compressed and uncompressed), "
operator|+
literal|"Text(Input|Output)Format (compressed and uncompressed)"
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
name|JHLogAnalyzer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|JHLogAnalyzer
operator|.
name|class
argument_list|,
literal|"Job History Log analyzer."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
name|SliveTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|SliveTest
operator|.
name|class
argument_list|,
literal|"HDFS Stress Test and Live Data Verification."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|run (String argv[])
specifier|public
name|void
name|run
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
try|try
block|{
name|pgd
operator|.
name|driver
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
operator|new
name|MapredTestDriver
argument_list|()
operator|.
name|run
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

