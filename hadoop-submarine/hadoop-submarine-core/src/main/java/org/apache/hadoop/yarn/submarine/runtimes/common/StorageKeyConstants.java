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

begin_class
DECL|class|StorageKeyConstants
specifier|public
class|class
name|StorageKeyConstants
block|{
DECL|field|JOB_NAME
specifier|public
specifier|static
specifier|final
name|String
name|JOB_NAME
init|=
literal|"JOB_NAME"
decl_stmt|;
DECL|field|JOB_RUN_ARGS
specifier|public
specifier|static
specifier|final
name|String
name|JOB_RUN_ARGS
init|=
literal|"JOB_RUN_ARGS"
decl_stmt|;
DECL|field|APPLICATION_ID
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_ID
init|=
literal|"APPLICATION_ID"
decl_stmt|;
DECL|field|CHECKPOINT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CHECKPOINT_PATH
init|=
literal|"CHECKPOINT_PATH"
decl_stmt|;
DECL|field|INPUT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_PATH
init|=
literal|"INPUT_PATH"
decl_stmt|;
DECL|field|SAVED_MODEL_PATH
specifier|public
specifier|static
specifier|final
name|String
name|SAVED_MODEL_PATH
init|=
literal|"SAVED_MODEL_PATH"
decl_stmt|;
block|}
end_class

end_unit

