begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.resourceplugin.gpu
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|resourceplugin
operator|.
name|gpu
package|;
end_package

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
name|util
operator|.
name|HashMap
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
name|Shell
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformation
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|gpu
operator|.
name|GpuDeviceInformationParser
import|;
end_import

begin_comment
comment|/**  * Executes the "nvidia-smi" command and returns an object  * based on its output.  *  */
end_comment

begin_class
DECL|class|NvidiaBinaryHelper
specifier|public
class|class
name|NvidiaBinaryHelper
block|{
comment|/**    * command should not run more than 10 sec.    */
DECL|field|MAX_EXEC_TIMEOUT_MS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_EXEC_TIMEOUT_MS
init|=
literal|10
operator|*
literal|1000
decl_stmt|;
comment|/**    * @param pathOfGpuBinary The path of the binary    * @return the GpuDeviceInformation parsed from the nvidia-smi output    * @throws IOException if the binary output is not readable    * @throws YarnException if the pathOfGpuBinary is null,    * or the output parse failed    */
DECL|method|getGpuDeviceInformation ( String pathOfGpuBinary)
specifier|synchronized
name|GpuDeviceInformation
name|getGpuDeviceInformation
parameter_list|(
name|String
name|pathOfGpuBinary
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|GpuDeviceInformationParser
name|parser
init|=
operator|new
name|GpuDeviceInformationParser
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathOfGpuBinary
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to find GPU discovery executable, please double check "
operator|+
name|YarnConfiguration
operator|.
name|NM_GPU_PATH_TO_EXEC
operator|+
literal|" setting."
argument_list|)
throw|;
block|}
name|String
name|output
init|=
name|Shell
operator|.
name|execCommand
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|pathOfGpuBinary
block|,
literal|"-x"
block|,
literal|"-q"
block|}
argument_list|,
name|MAX_EXEC_TIMEOUT_MS
argument_list|)
decl_stmt|;
return|return
name|parser
operator|.
name|parseXml
argument_list|(
name|output
argument_list|)
return|;
block|}
block|}
end_class

end_unit

