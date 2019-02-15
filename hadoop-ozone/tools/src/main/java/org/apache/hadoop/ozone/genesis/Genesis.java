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
name|Options
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

begin_comment
comment|/**  * Main class that executes a set of HDDS/Ozone benchmarks.  * We purposefully don't use the runner and tools classes from Hadoop.  * There are some name collisions with OpenJDK JMH package.  *<p>  * Hence, these classes do not use the Tool/Runner pattern of standard Hadoop  * CLI.  */
end_comment

begin_class
DECL|class|Genesis
specifier|public
specifier|final
class|class
name|Genesis
block|{
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
name|Options
name|opt
init|=
operator|new
name|OptionsBuilder
argument_list|()
operator|.
name|include
argument_list|(
name|BenchMarkContainerStateMap
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|include
argument_list|(
name|BenchMarkOMKeyAllocation
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|include
argument_list|(
name|BenchMarkBlockManager
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
comment|//        .include(BenchMarkMetadataStoreReads.class.getSimpleName())
comment|//        .include(BenchMarkMetadataStoreWrites.class.getSimpleName())
comment|//        .include(BenchMarkDatanodeDispatcher.class.getSimpleName())
comment|// Commenting this test out, till we support either a command line or a config
comment|// file based ability to run tests.
comment|//        .include(BenchMarkRocksDbStore.class.getSimpleName())
operator|.
name|warmupIterations
argument_list|(
literal|5
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
name|build
argument_list|()
decl_stmt|;
operator|new
name|Runner
argument_list|(
name|opt
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

