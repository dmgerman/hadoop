begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli
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
name|client
operator|.
name|cli
package|;
end_package

begin_comment
comment|/*  * NOTE: use lowercase + "_" for the option name  */
end_comment

begin_class
DECL|class|CliConstants
specifier|public
class|class
name|CliConstants
block|{
DECL|field|RUN
specifier|public
specifier|static
specifier|final
name|String
name|RUN
init|=
literal|"run"
decl_stmt|;
DECL|field|SERVE
specifier|public
specifier|static
specifier|final
name|String
name|SERVE
init|=
literal|"serve"
decl_stmt|;
DECL|field|LIST
specifier|public
specifier|static
specifier|final
name|String
name|LIST
init|=
literal|"list"
decl_stmt|;
DECL|field|SHOW
specifier|public
specifier|static
specifier|final
name|String
name|SHOW
init|=
literal|"show"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|INPUT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_PATH
init|=
literal|"input_path"
decl_stmt|;
DECL|field|CHECKPOINT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CHECKPOINT_PATH
init|=
literal|"checkpoint_path"
decl_stmt|;
DECL|field|SAVED_MODEL_PATH
specifier|public
specifier|static
specifier|final
name|String
name|SAVED_MODEL_PATH
init|=
literal|"saved_model_path"
decl_stmt|;
DECL|field|N_WORKERS
specifier|public
specifier|static
specifier|final
name|String
name|N_WORKERS
init|=
literal|"num_workers"
decl_stmt|;
DECL|field|N_SERVING_TASKS
specifier|public
specifier|static
specifier|final
name|String
name|N_SERVING_TASKS
init|=
literal|"num_serving_tasks"
decl_stmt|;
DECL|field|N_PS
specifier|public
specifier|static
specifier|final
name|String
name|N_PS
init|=
literal|"num_ps"
decl_stmt|;
DECL|field|WORKER_RES
specifier|public
specifier|static
specifier|final
name|String
name|WORKER_RES
init|=
literal|"worker_resources"
decl_stmt|;
DECL|field|SERVING_RES
specifier|public
specifier|static
specifier|final
name|String
name|SERVING_RES
init|=
literal|"serving_resources"
decl_stmt|;
DECL|field|PS_RES
specifier|public
specifier|static
specifier|final
name|String
name|PS_RES
init|=
literal|"ps_resources"
decl_stmt|;
DECL|field|DOCKER_IMAGE
specifier|public
specifier|static
specifier|final
name|String
name|DOCKER_IMAGE
init|=
literal|"docker_image"
decl_stmt|;
DECL|field|QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE
init|=
literal|"queue"
decl_stmt|;
DECL|field|TENSORBOARD
specifier|public
specifier|static
specifier|final
name|String
name|TENSORBOARD
init|=
literal|"tensorboard"
decl_stmt|;
DECL|field|TENSORBOARD_RESOURCES
specifier|public
specifier|static
specifier|final
name|String
name|TENSORBOARD_RESOURCES
init|=
literal|"tensorboard_resources"
decl_stmt|;
DECL|field|TENSORBOARD_DEFAULT_RESOURCES
specifier|public
specifier|static
specifier|final
name|String
name|TENSORBOARD_DEFAULT_RESOURCES
init|=
literal|"memory=4G,vcores=1"
decl_stmt|;
DECL|field|WORKER_LAUNCH_CMD
specifier|public
specifier|static
specifier|final
name|String
name|WORKER_LAUNCH_CMD
init|=
literal|"worker_launch_cmd"
decl_stmt|;
DECL|field|SERVING_LAUNCH_CMD
specifier|public
specifier|static
specifier|final
name|String
name|SERVING_LAUNCH_CMD
init|=
literal|"serving_launch_cmd"
decl_stmt|;
DECL|field|PS_LAUNCH_CMD
specifier|public
specifier|static
specifier|final
name|String
name|PS_LAUNCH_CMD
init|=
literal|"ps_launch_cmd"
decl_stmt|;
DECL|field|ENV
specifier|public
specifier|static
specifier|final
name|String
name|ENV
init|=
literal|"env"
decl_stmt|;
DECL|field|VERBOSE
specifier|public
specifier|static
specifier|final
name|String
name|VERBOSE
init|=
literal|"verbose"
decl_stmt|;
DECL|field|SERVING_FRAMEWORK
specifier|public
specifier|static
specifier|final
name|String
name|SERVING_FRAMEWORK
init|=
literal|"serving_framework"
decl_stmt|;
DECL|field|STOP
specifier|public
specifier|static
specifier|final
name|String
name|STOP
init|=
literal|"stop"
decl_stmt|;
DECL|field|WAIT_JOB_FINISH
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_JOB_FINISH
init|=
literal|"wait_job_finish"
decl_stmt|;
DECL|field|PS_DOCKER_IMAGE
specifier|public
specifier|static
specifier|final
name|String
name|PS_DOCKER_IMAGE
init|=
literal|"ps_docker_image"
decl_stmt|;
DECL|field|WORKER_DOCKER_IMAGE
specifier|public
specifier|static
specifier|final
name|String
name|WORKER_DOCKER_IMAGE
init|=
literal|"worker_docker_image"
decl_stmt|;
DECL|field|TENSORBOARD_DOCKER_IMAGE
specifier|public
specifier|static
specifier|final
name|String
name|TENSORBOARD_DOCKER_IMAGE
init|=
literal|"tensorboard_docker_image"
decl_stmt|;
block|}
end_class

end_unit

