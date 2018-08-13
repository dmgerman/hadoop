begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common
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
name|common
package|;
end_package

begin_class
DECL|class|Envs
specifier|public
class|class
name|Envs
block|{
DECL|field|TASK_TYPE_ENV
specifier|public
specifier|static
specifier|final
name|String
name|TASK_TYPE_ENV
init|=
literal|"_TASK_TYPE"
decl_stmt|;
DECL|field|TASK_INDEX_ENV
specifier|public
specifier|static
specifier|final
name|String
name|TASK_INDEX_ENV
init|=
literal|"_TASK_INDEX"
decl_stmt|;
comment|/*    * HDFS/HADOOP-related configs    */
DECL|field|HADOOP_HDFS_HOME
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_HDFS_HOME
init|=
literal|"HADOOP_HDFS_HOME"
decl_stmt|;
DECL|field|JAVA_HOME
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_HOME
init|=
literal|"JAVA_HOME"
decl_stmt|;
DECL|field|HADOOP_CONF_DIR
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_CONF_DIR
init|=
literal|"HADOOP_CONF_DIR"
decl_stmt|;
block|}
end_class

end_unit

