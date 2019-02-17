begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
operator|.
name|common
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Persistent job/model, etc.  */
end_comment

begin_class
DECL|class|SubmarineStorage
specifier|public
specifier|abstract
class|class
name|SubmarineStorage
block|{
comment|/**    * Add a new job by name    * @param jobName name of job.    * @param jobInfo info of the job.    */
DECL|method|addNewJob (String jobName, Map<String, String> jobInfo)
specifier|public
specifier|abstract
name|void
name|addNewJob
parameter_list|(
name|String
name|jobName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jobInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get job info by job name.    * @param jobName name of job    * @return info of the job.    */
DECL|method|getJobInfoByName (String jobName)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getJobInfoByName
parameter_list|(
name|String
name|jobName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add a new model    * @param modelName name of model    * @param version version of the model, when null is specified, it will be    *                "default"    * @param modelInfo info of the model.    */
DECL|method|addNewModel (String modelName, String version, Map<String, String> modelInfo)
specifier|public
specifier|abstract
name|void
name|addNewModel
parameter_list|(
name|String
name|modelName
parameter_list|,
name|String
name|version
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|modelInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get model info by name and version.    *  @param modelName name of model.    * @param version version of the model, when null is specifed, it will be    */
DECL|method|getModelInfoByName (String modelName, String version)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getModelInfoByName
parameter_list|(
name|String
name|modelName
parameter_list|,
name|String
name|version
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

