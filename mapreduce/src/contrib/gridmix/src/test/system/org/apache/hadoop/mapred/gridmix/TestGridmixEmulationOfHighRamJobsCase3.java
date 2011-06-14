begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|logging
operator|.
name|Log
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
name|gridmix
operator|.
name|test
operator|.
name|system
operator|.
name|GridMixConfig
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
name|gridmix
operator|.
name|GridmixJob
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
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Run the {@link Gridmix} with a high ram jobs trace and   * verify each {@link Gridmix} job whether it honors the high ram or not.  * In the trace the jobs should use the high ram only for reducers.  */
end_comment

begin_class
DECL|class|TestGridmixEmulationOfHighRamJobsCase3
specifier|public
class|class
name|TestGridmixEmulationOfHighRamJobsCase3
extends|extends
name|GridmixSystemTestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestGridmixEmulationOfHighRamJobsCase3
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Generate input data and run {@link Gridmix} with a high ram jobs trace     * as a load job and SERIAL submission policy in a SubmitterUserResolver     * mode. Verify each {@link Gridmix} job whether it honors the     * high ram or not after completion of execution.     * @throws Exception - if an error occurs.    */
annotation|@
name|Test
DECL|method|testEmulationOfHighRamForReducersOfMRJobs ()
specifier|public
name|void
name|testEmulationOfHighRamForReducersOfMRJobs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|inputSizeInMB
init|=
name|cSize
operator|*
literal|250
decl_stmt|;
name|String
name|tracePath
init|=
name|getTraceFile
argument_list|(
literal|"highram_mr_jobs_case3"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Trace file has not found."
argument_list|,
name|tracePath
argument_list|)
expr_stmt|;
name|String
index|[]
name|runtimeValues
init|=
block|{
literal|"LOADJOB"
block|,
name|SubmitterUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"SERIAL"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
name|tracePath
block|}
decl_stmt|;
name|String
index|[]
name|otherArgs
init|=
block|{
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_DISTCACHE_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_COMPRESSION_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridmixJob
operator|.
name|GRIDMIX_HIGHRAM_EMULATION_ENABLE
operator|+
literal|"=true"
block|}
decl_stmt|;
name|validateTaskMemoryParamters
argument_list|(
name|tracePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runGridmixAndVerify
argument_list|(
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|,
name|tracePath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

