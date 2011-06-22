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
name|Log
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
name|LogFactory
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
name|test
operator|.
name|system
operator|.
name|GridMixRunMode
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
name|UtilsForGridmix
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

begin_comment
comment|/**  * Verify the gridmix jobs compression ratio's of reduce output and   * with default and custom ratios.  */
end_comment

begin_class
DECL|class|TestCompressionEmulationForUncompressInAndCompressOut
specifier|public
class|class
name|TestCompressionEmulationForUncompressInAndCompressOut
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
literal|"TestCompressionEmulationForUncompressInAndCompressOut.class"
argument_list|)
decl_stmt|;
DECL|field|inputSizeInMB
specifier|final
name|long
name|inputSizeInMB
init|=
literal|1024
operator|*
literal|6
decl_stmt|;
comment|/**    * Generate a uncompressed input data and verify the compression ratios     * of reduce output against default output compression ratio.    * @throws Exception -if an error occurs.    */
annotation|@
name|Test
DECL|method|testCompressionEmulationOfCompressedOuputWithDefaultRatios ()
specifier|public
name|void
name|testCompressionEmulationOfCompressedOuputWithDefaultRatios
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|tracePath
init|=
name|getTraceFile
argument_list|(
literal|"compression_case3_trace"
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
specifier|final
name|String
index|[]
name|runtimeValues
init|=
block|{
literal|"LOADJOB"
block|,
name|RoundRobinUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"REPLAY"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
literal|"file://"
operator|+
name|UtilsForGridmix
operator|.
name|getProxyUsersFile
argument_list|(
name|conf
argument_list|)
block|,
name|tracePath
block|}
decl_stmt|;
specifier|final
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
name|GridmixJob
operator|.
name|GRIDMIX_HIGHRAM_EMULATION_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_COMPRESSION_ENABLE
operator|+
literal|"=true"
block|}
decl_stmt|;
name|runGridmixAndVerify
argument_list|(
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|,
name|tracePath
argument_list|,
name|GridMixRunMode
operator|.
name|DATA_GENERATION_AND_RUN_GRIDMIX
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use existing uncompressed input data and verify the compression ratio     * of reduce output against custom output compression ratio and also verify     * the compression output file output format.    * @throws Exception -if an error occurs.    */
annotation|@
name|Test
DECL|method|testCompressionEmulationOfCompressedOutputWithCustomRatios ()
specifier|public
name|void
name|testCompressionEmulationOfCompressedOutputWithCustomRatios
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|tracePath
init|=
name|getTraceFile
argument_list|(
literal|"compression_case3_trace"
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
name|UtilsForGridmix
operator|.
name|cleanup
argument_list|(
name|gridmixDir
argument_list|,
name|rtClient
operator|.
name|getDaemonConf
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
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
literal|"STRESS"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
name|tracePath
block|}
decl_stmt|;
specifier|final
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
literal|"=true"
block|,
literal|"-D"
block|,
name|GridmixJob
operator|.
name|GRIDMIX_HIGHRAM_EMULATION_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_OUTPUT_COMPRESSION_RATIO
operator|+
literal|"=0.38"
block|}
decl_stmt|;
name|runGridmixAndVerify
argument_list|(
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|,
name|tracePath
argument_list|,
name|GridMixRunMode
operator|.
name|DATA_GENERATION_AND_RUN_GRIDMIX
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

