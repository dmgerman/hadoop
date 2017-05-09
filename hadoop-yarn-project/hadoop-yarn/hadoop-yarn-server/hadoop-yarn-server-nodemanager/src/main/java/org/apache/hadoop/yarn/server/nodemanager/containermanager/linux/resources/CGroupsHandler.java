begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|linux
operator|.
name|resources
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Provides CGroups functionality. Implementations are expected to be  * thread-safe  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|CGroupsHandler
specifier|public
interface|interface
name|CGroupsHandler
block|{
comment|/**    * List of supported cgroup subsystem types.    */
DECL|enum|CGroupController
enum|enum
name|CGroupController
block|{
DECL|enumConstant|CPU
name|CPU
argument_list|(
literal|"cpu"
argument_list|)
block|,
DECL|enumConstant|NET_CLS
name|NET_CLS
argument_list|(
literal|"net_cls"
argument_list|)
block|,
DECL|enumConstant|BLKIO
name|BLKIO
argument_list|(
literal|"blkio"
argument_list|)
block|,
DECL|enumConstant|MEMORY
name|MEMORY
argument_list|(
literal|"memory"
argument_list|)
block|,
DECL|enumConstant|CPUACCT
name|CPUACCT
argument_list|(
literal|"cpuacct"
argument_list|)
block|,
DECL|enumConstant|CPUSET
name|CPUSET
argument_list|(
literal|"cpuset"
argument_list|)
block|,
DECL|enumConstant|FREEZER
name|FREEZER
argument_list|(
literal|"freezer"
argument_list|)
block|,
DECL|enumConstant|DEVICES
name|DEVICES
argument_list|(
literal|"devices"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|CGroupController (String name)
name|CGroupController
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getName ()
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
DECL|field|CGROUP_FILE_TASKS
name|String
name|CGROUP_FILE_TASKS
init|=
literal|"tasks"
decl_stmt|;
DECL|field|CGROUP_PARAM_CLASSID
name|String
name|CGROUP_PARAM_CLASSID
init|=
literal|"classid"
decl_stmt|;
DECL|field|CGROUP_PARAM_BLKIO_WEIGHT
name|String
name|CGROUP_PARAM_BLKIO_WEIGHT
init|=
literal|"weight"
decl_stmt|;
DECL|field|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
name|String
name|CGROUP_PARAM_MEMORY_HARD_LIMIT_BYTES
init|=
literal|"limit_in_bytes"
decl_stmt|;
DECL|field|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
name|String
name|CGROUP_PARAM_MEMORY_SOFT_LIMIT_BYTES
init|=
literal|"soft_limit_in_bytes"
decl_stmt|;
DECL|field|CGROUP_PARAM_MEMORY_SWAPPINESS
name|String
name|CGROUP_PARAM_MEMORY_SWAPPINESS
init|=
literal|"swappiness"
decl_stmt|;
DECL|field|CGROUP_CPU_PERIOD_US
name|String
name|CGROUP_CPU_PERIOD_US
init|=
literal|"cfs_period_us"
decl_stmt|;
DECL|field|CGROUP_CPU_QUOTA_US
name|String
name|CGROUP_CPU_QUOTA_US
init|=
literal|"cfs_quota_us"
decl_stmt|;
DECL|field|CGROUP_CPU_SHARES
name|String
name|CGROUP_CPU_SHARES
init|=
literal|"shares"
decl_stmt|;
comment|/**    * Mounts or initializes a cgroup controller.    * @param controller - the controller being initialized    * @throws ResourceHandlerException the initialization failed due to the    * environment    */
DECL|method|initializeCGroupController (CGroupController controller)
name|void
name|initializeCGroupController
parameter_list|(
name|CGroupController
name|controller
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Creates a cgroup for a given controller.    * @param controller - controller type for which the cgroup is being created    * @param cGroupId - id of the cgroup being created    * @return full path to created cgroup    * @throws ResourceHandlerException creation failed    */
DECL|method|createCGroup (CGroupController controller, String cGroupId)
name|String
name|createCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Deletes the specified cgroup.    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup being deleted    * @throws ResourceHandlerException deletion failed    */
DECL|method|deleteCGroup (CGroupController controller, String cGroupId)
name|void
name|deleteCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * Gets the relative path for the cgroup, independent of a controller, for a    * given cgroup id.    * @param cGroupId - id of the cgroup    * @return path for the cgroup relative to the root of (any) controller.    */
DECL|method|getRelativePathForCGroup (String cGroupId)
name|String
name|getRelativePathForCGroup
parameter_list|(
name|String
name|cGroupId
parameter_list|)
function_decl|;
comment|/**    * Gets the full path for the cgroup, given a controller and a cgroup id.    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup    * @return full path for the cgroup    */
DECL|method|getPathForCGroup (CGroupController controller, String cGroupId)
name|String
name|getPathForCGroup
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
function_decl|;
comment|/**    * Gets the full path for the cgroup's tasks file, given a controller and a    * cgroup id.    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup    * @return full path for the cgroup's tasks file    */
DECL|method|getPathForCGroupTasks (CGroupController controller, String cGroupId)
name|String
name|getPathForCGroupTasks
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|)
function_decl|;
comment|/**    * Gets the full path for a cgroup parameter, given a controller,    * cgroup id and parameter name.    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup    * @param param - cgroup parameter ( e.g classid )    * @return full path for the cgroup parameter    */
DECL|method|getPathForCGroupParam (CGroupController controller, String cGroupId, String param)
name|String
name|getPathForCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|)
function_decl|;
comment|/**    * updates a cgroup parameter, given a controller, cgroup id, parameter name.    * and a parameter value    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup    * @param param - cgroup parameter ( e.g classid )    * @param value - value to be written to the parameter file    * @throws ResourceHandlerException the operation failed    */
DECL|method|updateCGroupParam (CGroupController controller, String cGroupId, String param, String value)
name|void
name|updateCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
comment|/**    * reads a cgroup parameter value, given a controller, cgroup id, parameter.    * name    * @param controller - controller type for the cgroup    * @param cGroupId - id of the cgroup    * @param param - cgroup parameter ( e.g classid )    * @return parameter value as read from the parameter file    * @throws ResourceHandlerException the operation failed    */
DECL|method|getCGroupParam (CGroupController controller, String cGroupId, String param)
name|String
name|getCGroupParam
parameter_list|(
name|CGroupController
name|controller
parameter_list|,
name|String
name|cGroupId
parameter_list|,
name|String
name|param
parameter_list|)
throws|throws
name|ResourceHandlerException
function_decl|;
block|}
end_interface

end_unit

