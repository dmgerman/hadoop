begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.genesis
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|genesis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|profile
operator|.
name|StackProfiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|RunnerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|options
operator|.
name|OptionsBuilder
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_comment
comment|/**  * Main class that executes a set of HDDS/Ozone benchmarks.  * We purposefully don't use the runner and tools classes from Hadoop.  * There are some name collisions with OpenJDK JMH package.  *<p>  * Hence, these classes do not use the Tool/Runner pattern of standard Hadoop  * CLI.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"ozone genesis"
argument_list|,
name|description
operator|=
literal|"Tool for running ozone benchmarks"
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|)
DECL|class|Genesis
specifier|public
specifier|final
class|class
name|Genesis
block|{
comment|// After adding benchmark in genesis package add the benchmark name in the
comment|// description for this option.
annotation|@
name|Option
argument_list|(
name|names
operator|=
literal|"-benchmark"
argument_list|,
name|split
operator|=
literal|","
argument_list|,
name|description
operator|=
literal|"Option used for specifying benchmarks to run.\n"
operator|+
literal|"Ex. ozone genesis -benchmark BenchMarkContainerStateMap,"
operator|+
literal|"BenchMarkOMKeyAllocation.\n"
operator|+
literal|"Possible benchmarks which can be used are "
operator|+
literal|"{BenchMarkContainerStateMap, BenchMarkOMKeyAllocation, "
operator|+
literal|"BenchMarkOzoneManager, BenchMarkOMClient, "
operator|+
literal|"BenchMarkSCM, BenchMarkMetadataStoreReads, "
operator|+
literal|"BenchMarkMetadataStoreWrites, BenchMarkDatanodeDispatcher, "
operator|+
literal|"BenchMarkRocksDbStore}"
argument_list|)
DECL|field|benchmarks
specifier|private
specifier|static
name|String
index|[]
name|benchmarks
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
literal|"-t"
argument_list|,
name|defaultValue
operator|=
literal|"4"
argument_list|,
name|description
operator|=
literal|"Number of threads to use for the benchmark.\n"
operator|+
literal|"This option can be overridden by threads mentioned in benchmark."
argument_list|)
DECL|field|numThreads
specifier|private
specifier|static
name|int
name|numThreads
decl_stmt|;
DECL|method|Genesis ()
specifier|private
name|Genesis
parameter_list|()
block|{   }
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
name|RunnerException
block|{
name|CommandLine
name|commandLine
init|=
operator|new
name|CommandLine
argument_list|(
operator|new
name|Genesis
argument_list|()
argument_list|)
decl_stmt|;
name|commandLine
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|commandLine
operator|.
name|isUsageHelpRequested
argument_list|()
condition|)
block|{
name|commandLine
operator|.
name|usage
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return;
block|}
name|OptionsBuilder
name|optionsBuilder
init|=
operator|new
name|OptionsBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|benchmarks
operator|!=
literal|null
condition|)
block|{
comment|// The OptionsBuilder#include takes a regular expression as argument.
comment|// Therefore it is important to keep the benchmark names unique for
comment|// running a benchmark. For example if there are two benchmarks -
comment|// BenchMarkOM and BenchMarkOMClient and we include BenchMarkOM then
comment|// both the benchmarks will be run.
for|for
control|(
name|String
name|benchmark
range|:
name|benchmarks
control|)
block|{
name|optionsBuilder
operator|.
name|include
argument_list|(
name|benchmark
argument_list|)
expr_stmt|;
block|}
block|}
name|optionsBuilder
operator|.
name|warmupIterations
argument_list|(
literal|2
argument_list|)
operator|.
name|measurementIterations
argument_list|(
literal|20
argument_list|)
operator|.
name|addProfiler
argument_list|(
name|StackProfiler
operator|.
name|class
argument_list|)
operator|.
name|shouldDoGC
argument_list|(
literal|true
argument_list|)
operator|.
name|forks
argument_list|(
literal|1
argument_list|)
operator|.
name|threads
argument_list|(
name|numThreads
argument_list|)
expr_stmt|;
operator|new
name|Runner
argument_list|(
name|optionsBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

